function Check() {
    var price = document.getElementById("price").Value;
    var crash = document.getElementById("crash").Value;
    if (price == null) {
        document.getElementById("price").defaultValue = 0;
    }
    if (crash == null) {
        document.getElementById("crash").defaultValue = 0;
    }
}