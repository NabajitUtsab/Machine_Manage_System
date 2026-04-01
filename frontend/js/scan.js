// ==================== QR SCANNER ====================
let html5QrCode = null;
let isScanning = false;
let currentMachine = null;

function startScanner() {
    const reader = document.getElementById("reader");

    if (isScanning) {
        alert("Scanner is already running");
        return;
    }

    if (!html5QrCode) {
        html5QrCode = new Html5Qrcode("reader");
    }

    html5QrCode.start(
        { facingMode: "environment" },
        {
            fps: 10,
            qrbox: { width: 250, height: 250 }
        },
        onScanSuccess,
        onScanFailure
    ).then(() => {
        isScanning = true;
        document.getElementById("startBtn").style.display = "none";
        document.getElementById("stopBtn").style.display = "inline-block";
    }).catch(err => {
        alert("Unable to start camera: " + err);
    });
}

function stopScanner() {
    if (html5QrCode && isScanning) {
        html5QrCode.stop().then(() => {
            isScanning = false;
            document.getElementById("startBtn").style.display = "inline-block";
            document.getElementById("stopBtn").style.display = "none";
        }).catch(err => {
            console.error("Error stopping scanner:", err);
        });
    }
}

async function onScanSuccess(decodedText, decodedResult) {
    stopScanner();
    const machineId = decodedText.trim();
    await fetchMachineDetails(machineId);
}

function onScanFailure(error) {
    // Silently handle scan failures
}

async function fetchMachineDetails(machineId) {
    try {
        const machine = await apiRequest(`/machines/${machineId}`);
        currentMachine = machine;
        displayMachineDetails(machine);
        document.getElementById("viewQRBtn").style.display = "inline-block";
    } catch (err) {
        const detailsDiv = document.getElementById("machineDetails");
        detailsDiv.innerHTML = `<p class="error">Machine not found: ${err.message}</p>`;
        document.getElementById("viewQRBtn").style.display = "none";
    }
}

function displayMachineDetails(machine) {
    const detailsDiv = document.getElementById("machineDetails");

    detailsDiv.innerHTML = `
        <div class="machine-info">
            <h4>Scanned Machine Details</h4>
            <p><strong>ID:</strong> ${machine.id}</p>
            <p><strong>Code:</strong> ${machine.code}</p>
            <p><strong>Group:</strong> ${machine.groupName}</p>
            <p><strong>Status:</strong> <span class="status-badge status-${machine.status}">${machine.status}</span></p>
            <p><strong>Origin Concern:</strong> ${machine.originConcern?.name || '-'}</p>
            <p><strong>Current Concern:</strong> ${machine.currentConcern?.name || '-'}</p>
        </div>
    `;
}

function showQRModalFromScan() {
    if (!currentMachine) {
        alert("No machine scanned yet");
        return;
    }

    const qrContainer = document.getElementById("qrCodeContainer");
    const detailsContainer = document.getElementById("machineDetailsContainer");

    qrContainer.innerHTML = `<img src="${BASE_URL}/machines/qr/${currentMachine.id}" alt="QR Code" class="qr-image">`;

    detailsContainer.innerHTML = `
        <div class="machine-info">
            <h4>Machine Details</h4>
            <p><strong>ID:</strong> ${currentMachine.id}</p>
            <p><strong>Code:</strong> ${currentMachine.code}</p>
            <p><strong>Group:</strong> ${currentMachine.groupName}</p>
            <p><strong>Status:</strong> <span class="status-badge status-${currentMachine.status}">${currentMachine.status}</span></p>
            <p><strong>Origin Concern:</strong> ${currentMachine.originConcern?.name || '-'}</p>
            <p><strong>Current Concern:</strong> ${currentMachine.currentConcern?.name || '-'}</p>
        </div>
    `;

    showQRModal();
}

// Clean up on page unload
window.addEventListener('beforeunload', () => {
    if (html5QrCode && isScanning) {
        html5QrCode.stop();
    }
});