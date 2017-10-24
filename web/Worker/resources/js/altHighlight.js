/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function () {
    var table = $('#tblTransaction').dataTable({
         bJQueryUI: true,
         bFilter: false,
         sPaginationType: "full_numbers",
         "columnDefs": [
            {
                "targets": [ 2 ],
                "visible": false
            },
            {
                "targets": [ 3 ],
                "visible": false
            },
            {
                "targets": [ 4 ],
                "visible": false
            }
        ]
         
    });
    

 
});

$(document).ready(function () {
    var table = $('#tblAccounts').dataTable({
         bJQueryUI: true,
         bFilter: false,
         sPaginationType: "full_numbers"
         
    });
    

 
});

$(document).ready(function () {
    var table = $('#tblSearchUsers').dataTable({
         bJQueryUI: true,
         bFilter: false,
         sPaginationType: "full_numbers"
         
    });
});

