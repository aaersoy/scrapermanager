$(document).ready(function () {
    $('[data-toggle="tooltip"]').tooltip();
});

$("#newAppiumRequestButton").click(function (e) {
    e.preventDefault();
    window.location.href="/run?hashtagname="+$("#hashTagNameText").val()+"&tiktokfollowercount="+$("#tiktokFollowerCountText").val() + "&tiktoklikecount="+$("#tiktokLikeCount").val();
});

$('#newStopAppiumRequestButton').click(function (e) {
    e.preventDefault();
    window.location.href="/stopappium";
});


$('#searchButton').click(function (e) {
    e.preventDefault();
    window.location.href="/searchhashtag?hashtagname="+$("#searchText").val();
});

$('#exportAsCSV').click(function (e) {
    e.preventDefault();
    window.location.href="/exportascsv";
});




$('#adbDeviceButton').click(function (e) {
    e.preventDefault();
    window.location.href="/connectadbdevice?deviceid="+$("#adbDeviceText").val();
});

$('#allButton').click(function (e) {
    e.preventDefault();
    window.location.href="/all";
});

function editBotName(botId){
    var someItem;
    console.log("asdasd");
    someItem=document.getElementById(botId);
    someItem.remove();
}