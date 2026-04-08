    const draftId = new URLSearchParams(window.location.search).get('draftId');
    let performanceId = null;
    let memberId = null;
    let maxSelectableSeats = 0;
    const selectedSeats = new Map();
    let draftCache = null;

    async function initPage() {
        try {
            if (!draftId) {
                throw new Error('draftId가 없습니다.');
            }

            const draftResponse = await fetch(`/reservation-drafts/${draftId}`);
            if (!draftResponse.ok) {
                throw new Error('draft 조회 실패');
            }

            draftCache = await draftResponse.json();
            performanceId = draftCache.performanceId;
            memberId = draftCache.memberId;
            maxSelectableSeats = draftCache.remainingSeatLimit ?? 0;
            document.querySelector('.title').textContent = `${draftCache.performanceTitle} ${draftCache.performanceDate}`;

            const [seatsResponse, remainingResponse] = await Promise.all([
                fetch(`/performances/${performanceId}/seats`),
                fetch(`/performances/${performanceId}/remaining-seat-count?memberId=${memberId}`)
            ]);

            if (!seatsResponse.ok) {
                throw new Error('좌석 조회 실패');
            }

            if (!remainingResponse.ok) {
                throw new Error('잔여 예매 가능 좌석 수 조회 실패');
            }

            const seats = await seatsResponse.json();
            const remainingData = await remainingResponse.json();

            maxSelectableSeats = remainingData.remainingSeatCount;

            renderRemainingLimitInfo();
            renderSeats(seats);

            if (draftCache.selectedSeats) {
                draftCache.selectedSeats.forEach(seatNumber => {
                    const seat = seats.find(item => item.seatNumber === seatNumber);
                    if (seat) {
                        selectedSeats.set(seatNumber, seat);
                    }
                });
                syncSelectedSeatButtons();
            }

            renderSelectedSeats();
            updateCompleteButtonState();
        } catch (e) {
            console.error(e);
            alert('페이지 정보를 불러오지 못했습니다.');
        }
    }

    //뒤로가기(공연 상세화면)
    function goToDetail() {
        location.href = `/performances/${performanceId}`;
    }

    function syncSelectedSeatButtons() {
        selectedSeats.forEach((seat, seatNumber) => {
            const button = document.querySelector(`.seat[data-seat-number="${seatNumber}"]`);
            if (button) {
                button.classList.remove('available');
                button.classList.add('selected');
            }
        });
    }

    function renderRemainingLimitInfo() {
        const selectedCount = document.getElementById('selectedCount');
        const remainingInfo = document.getElementById('remainingInfo');

        selectedCount.textContent = `${selectedSeats.size} / ${maxSelectableSeats}`;
        remainingInfo.textContent = `남은 예매 가능 좌석 수: ${maxSelectableSeats}`;
    }

    function renderSeats(seats) {
        const leftSection = document.getElementById('leftSection');
        const rightSection = document.getElementById('rightSection');

        leftSection.innerHTML = '';
        rightSection.innerHTML = '';

        const parsedSeats = seats.map(seat => {
            const parsed = parseSeatNumber(seat.seatNumber);
            return {
                seatNumber: seat.seatNumber,
                seatStatus: seat.seatStatus,
                row: parsed.row,
                number: parsed.number
            };
        });

        const rows = [...new Set(parsedSeats.map(seat => seat.row))].sort();

        rows.forEach(row => {
            const rowSeats = parsedSeats
                .filter(seat => seat.row === row)
                .sort((a, b) => a.number - b.number);

            const leftRowSeats = rowSeats.filter(seat => seat.number >= 1 && seat.number <= 5);
            const rightRowSeats = rowSeats.filter(seat => seat.number >= 6 && seat.number <= 11);

            leftSection.appendChild(createRowDiv(row, leftRowSeats));
            rightSection.appendChild(createRowDiv(row, rightRowSeats));
        });
    }

    function parseSeatNumber(seatNumber) {
        const match = seatNumber.match(/^([A-Z]+)(\d+)$/);

        if (!match) {
            return { row: '', number: 0 };
        }

        return {
            row: match[1],
            number: parseInt(match[2], 10)
        };
    }

    function createRowDiv(rowName, seats) {
        const rowDiv = document.createElement('div');
        rowDiv.className = 'seat-row';

        const rowLabel = document.createElement('div');
        rowLabel.className = 'row-label';
        rowLabel.textContent = rowName;
        rowDiv.appendChild(rowLabel);

        seats.forEach(seat => {
            const button = document.createElement('button');
            button.dataset.seatNumber = seat.seatNumber;
            button.textContent = '';
            button.type = 'button';

            const statusClass = convertSeatStatus(seat.seatStatus);
            button.className = `seat ${statusClass}`;

            if (seat.seatStatus === 'RESERVED') {
                button.disabled = true;
            } else {
                button.addEventListener('click', () => toggleSeatSelection(seat, button));
            }

            rowDiv.appendChild(button);
        });

        return rowDiv;
    }

    function convertSeatStatus(seatStatus) {
        if (seatStatus === 'AVAILABLE') return 'available';
        if (seatStatus === 'RESERVED') return 'reserved';
        return 'available';
    }

    function toggleSeatSelection(seat, button) {
        if (maxSelectableSeats <= 0) {
            alert('이 공연은 더 이상 예매할 수 없습니다. 1인 최대 4좌석까지 가능합니다.');
            return;
        }

        const seatNumber = seat.seatNumber;

        if (selectedSeats.has(seatNumber)) {
            selectedSeats.delete(seatNumber);
            button.classList.remove('selected');
            button.classList.add('available');
        } else {
            if (selectedSeats.size >= maxSelectableSeats) {
                alert(`이 공연에서 현재 최대 ${maxSelectableSeats}좌석까지 추가 예매할 수 있습니다.`);
                return;
            }

            selectedSeats.set(seatNumber, seat);
            button.classList.remove('available');
            button.classList.add('selected');
        }

        renderRemainingLimitInfo();
        renderSelectedSeats();
        updateCompleteButtonState();
    }

    function renderSelectedSeats() {
        const selectedSeatBox = document.getElementById('selectedSeatBox');
        selectedSeatBox.innerHTML = '';

        if (selectedSeats.size === 0) {
            selectedSeatBox.innerHTML = '<div class="empty-message">선택한 좌석이 없습니다.</div>';
            return;
        }

        selectedSeats.forEach(seat => {
            const item = document.createElement('div');
            item.className = 'selected-seat-item';

            const left = document.createElement('div');
            left.className = 'selected-seat-left';

            const seatName = document.createElement('span');
            seatName.textContent = `${seat.seatNumber}석`;

            const price = document.createElement('span');
            price.textContent = `${(draftCache?.performancePrice ?? 0).toLocaleString()}원`;

            left.appendChild(seatName);
            left.appendChild(price);

            const removeBtn = document.createElement('button');
            removeBtn.className = 'remove-btn';
            removeBtn.type = 'button';
            removeBtn.textContent = '×';
            removeBtn.addEventListener('click', () => removeSeat(seat.seatNumber));

            item.appendChild(left);
            item.appendChild(removeBtn);

            selectedSeatBox.appendChild(item);
        });
    }

    function removeSeat(seatNumber) {
        selectedSeats.delete(seatNumber);

        const targetBtn = document.querySelector(`.seat[data-seat-number="${seatNumber}"]`);
        if (targetBtn) {
            targetBtn.classList.remove('selected');
            targetBtn.classList.add('available');
        }

        renderRemainingLimitInfo();
        renderSelectedSeats();
        updateCompleteButtonState();
    }

    function updateCompleteButtonState() {
        const completeBtn = document.getElementById('completeBtn');
        completeBtn.disabled = selectedSeats.size === 0;
    }

    async function reloadSeatPageState() {
        selectedSeats.clear();
        await initPage();
    }

    document.getElementById('completeBtn').addEventListener('click', async () => {
        if (selectedSeats.size === 0) {
            alert('좌석을 선택해주세요.');
            return;
        }

        const seatNumbers = Array.from(selectedSeats.keys());

        try {
            const totalPrice = selectedSeats.size * (draftCache?.performancePrice ?? 0);

            const response = await fetch(`/reservation-drafts/${draftId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    selectedSeats: seatNumbers,
                    totalPrice: totalPrice
                })
            });

            if (!response.ok) {
                let errorPayload = null;
                try {
                    errorPayload = await response.json();
                } catch (jsonError) {
                    errorPayload = null;
                }

                if (response.status === 409 && errorPayload?.conflictedSeats?.length) {
                    const conflictedSeats = errorPayload.conflictedSeats.join(', ');
                    alert(`이미 다른 사용자가 선점한 좌석이 있습니다.\n충돌 좌석: ${conflictedSeats}`);
                    await reloadSeatPageState();
                    return;
                }

                throw new Error(errorPayload?.message || '선택 좌석 저장 실패');
            }

            await response.json();
            window.location.href = `/reservationConfirm2?draftId=${draftId}`;
        } catch (e) {
            console.error(e);
            alert(e.message || '다음 페이지로 이동하는 중 오류가 발생했습니다.');
        }
    });

    initPage();
