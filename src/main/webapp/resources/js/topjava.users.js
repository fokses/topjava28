const userAjaxUrl = "admin/users/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: userAjaxUrl
};

// $(document).ready(function () {
$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "email"
                },
                {
                    "data": "roles"
                },
                {
                    "data": "enabled"
                },
                {
                    "data": "registered"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ]
        })
    );

    $(".userEnabled").change(function() {
        var id = $(this).closest('tr').attr("id");

        $.ajax({
            url: ctx.ajaxUrl + "set-enabled/" + id + "/?enabled=" + this.checked,
            type: "PATCH"
        }).done(function () {
            updateTable();

            var columnNumber = 3; //first column
            $.each($("#datatable").find("tr"), function(){
                var tdOfCurrentColumn = $(this).children().eq(columnNumber);
            });

            successNoty("User enable status updated");
        });
    })
});