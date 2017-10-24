/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {
    var table = $('#tblSearchUsers').dataTable({
         bJQueryUI: true,
         bFilter: false,
         sPaginationType: "full_numbers"
    });
});
