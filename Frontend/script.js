async function saveVisitor() {
    const visitorName = document.getElementById("visitorName").value.trim();
    const phone = document.getElementById("phone").value.trim();
    const address = document.getElementById("address").value.trim();
    const idProof = document.getElementById("idProof").value.trim();
    const message = document.getElementById("message");

    if (!visitorName || !phone) {
        message.textContent = "Enter visitor name and phone.";
        return;
    }

    const data = new URLSearchParams({
        visitorName, phone, address, idProof
    });

    try {
        const response = await fetch("http://localhost:8080/api/visitor", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: data.toString()
        });

        const result = await response.text();
        message.textContent = result;

        if (response.ok) {
            message.textContent = "Visitor saved successfully!";
        }
    } catch (error) {
        message.textContent =
            "Could not connect to server. Check whether Java server is running.";
    }
}

async function saveHost() {
    const department = document.getElementById("department").value.trim();
    const hostName = document.getElementById("hostName").value.trim();
    const hostContact = document.getElementById("hostContact").value.trim();
    const message = document.getElementById("message");

    if (!department || !hostName || !hostContact) {
        message.textContent = "Please fill all fields.";
        return;
    }

    const data = new URLSearchParams({
        department,
        hostName,
        hostContact
    });

    try {
        const response = await fetch("http://localhost:8080/api/host", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: data.toString()
        });

        const result = await response.text();
        message.textContent = result;
    } catch (error) {
        message.textContent =
            "Could not connect to server. Check whether Java server is running.";
    }
}

async function saveVisit() {
    const visitorId = document.getElementById("visitorId").value.trim();
    const hostId = document.getElementById("hostId").value.trim();
    const visitDate = document.getElementById("visitDate").value;
    const purpose = document.getElementById("purpose").value.trim();
    const entryTime = document.getElementById("entryTime").value;
    const exitTime = document.getElementById("exitTime").value;
    const message = document.getElementById("message");

    if (!visitorId || !hostId || !visitDate || !purpose || !entryTime) {
        message.textContent = "Please fill all required fields.";
        return;
    }

    const data = new URLSearchParams({
        visitorId,
        hostId,
        visitDate,
        purpose,
        entryTime,
        exitTime
    });

    try {
        const response = await fetch("http://localhost:8080/api/visit", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: data.toString()
        });

        message.textContent = await response.text();
    } catch (error) {
        message.textContent =
            "Could not connect to server. Check whether Java server is running.";
    }
}


async function generatePass() {
    const visitId = document.getElementById("visitId").value.trim();
    const issuedAt = document.getElementById("issuedAt").value;
    const status = document.getElementById("passStatus").value;
    const message = document.getElementById("passMessage");

    if (!visitId || !issuedAt || !status) {
        message.textContent = "Please fill all fields.";
        return;
    }

    const data = new URLSearchParams({
        visitId,
        issuedAt,
        status
    });

    try {
        const response = await fetch(
            "http://localhost:8080/api/gatepass",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: data.toString()
            }
        );

        const result = await response.text();

        if (!response.ok) {
            message.textContent = result;
            return;
        }

        const match = result.match(/Pass ID:\s*(\d+)/);
        const passId = match ? match[1] : "Generated";

        message.innerHTML = `
            <h3>Gate Pass Generated Successfully!</h3>
            <p><b>Gate Pass ID:</b> ${passId}</p>
            <p><b>Visit ID:</b> ${visitId}</p>
            <p><b>Issue Date & Time:</b>
                ${issuedAt.replace("T", " ")}</p>
            <p><b>Status:</b> ${status}</p>
        `;
                document.getElementById("printButton").style.display = "inline-block";
    } catch (error) {
        message.textContent =
            "Could not connect to server. Check whether Java server is running.";
            
    }
}
