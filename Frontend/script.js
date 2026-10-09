function login() {
    let email = document.getElementById("email").value;
    let password = document.getElementById("password").value;

    if (email == "" || password == "") {
        document.getElementById("message").innerHTML =
            "Please enter email and password";
    } else {
        location.href = "dashboard.html";
    }
}

function saveVisitor() {
    alert("Visitor details saved");
}

function saveHost() {
    alert("Host details saved");
}

function saveVisit() {
    alert("Visit details saved");
}

function generatePass() {
    document.getElementById("passMessage").innerHTML =
        "Gate Pass Generated Successfully";
}