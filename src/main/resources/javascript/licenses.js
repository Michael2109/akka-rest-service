function createLicense(userID, totalActivations) {

    if(userID && totalActivations){

        var data = JSON.stringify({
            "userID":userID,
            "totalActivations":parseInt(totalActivations)
        });

        console.log(data);

        success = false;
        dataType = "application/json";
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            type: "POST",
            url: "http://localhost:8080/api/licenses/add-license",
            data: data,
            success: success,
            dataType: "json"
        });
    }

}

function search(userID){
    console.log("Searching for " + userID);

     $.get("http://localhost:8080/api/licenses", function(data, status){

     var licenses = data.licenses;

     console.log(licenses);

    var tableBody = document.getElementById('license-table-body');

    tableBody.innerHTML = "";

    for (var i = 0; i < licenses.length; i++) {
        var tr = "<tr>";

        tr += "<td>" + licenses[i].id + "</td>" + "<td>" + licenses[i].userID + "<td>" + licenses[i].key +"<td>" + licenses[i].activations +"<td>"+ licenses[i].totalActivations + "</td>";

        tr += "</tr>";

        /* We add the table row to the table body */
        tableBody.innerHTML += tr;
        console.log(tr);
    }
      });
}
