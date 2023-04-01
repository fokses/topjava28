const userAjaxUrl = "ajax/meals/";

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
                    "data": "dateTime"
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
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

    $("#filterForm").submit(function (event) {
        var formUrl = $(this).prop('action');

        updateTableByUrl(formUrl + "?" + $(this).serialize());
        event.preventDefault();
    });

    $("#filterReset").click(function(event) {
        $("#filterForm :input").each(function() {
            $(this).val("");
        })
    })
});