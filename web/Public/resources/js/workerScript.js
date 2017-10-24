/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {
    var table = $('#tblUsers').dataTable({
         bJQueryUI: true,
         bFilter: true,
         sPaginationType: "full_numbers"
    });
});
