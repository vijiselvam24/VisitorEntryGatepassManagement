
async function loadVisitors() {
    const visitorList = document.getElementById("visitorList");
    const message = document.getElementById("message");

    visitorList.innerHTML = "";
    message.textContent = "Loading visitors...";

    try {
        const response = await fetch(
            "http://localhost:8080/api/visitors"
        );

        if (!response.ok) {
            throw new Error("Failed to load visitors");
        }

        const visitors = await response.json();

        if (visitors.length === 0) {
            message.textContent = "No visitors found.";
            return;
        }

        const table = document.createElement("table");
        table.border = "1";

        table.innerHTML = `
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Phone</th>
                <th>Address</th>
                <th>ID Proof</th>
            </tr>
        `;

        visitors.forEach(visitor => {
            const row = table.insertRow();

            [
                visitor.visitor_id,
                visitor.visitor_name,
                visitor.phone,
                visitor.address,
                visitor.id_proof_type
            ].forEach(value => {
                const cell = row.insertCell();
                cell.textContent = value ?? "";
            });
        });

        visitorList.appendChild(table);
        message.textContent = "Visitors loaded successfully!";
    } catch (error) {
        message.textContent =
            "Could not load visitors. Please check whether the Java server is running.";
    }
}

