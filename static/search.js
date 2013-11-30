$( document ).ready(function() {
  $("#query").keyup(function() {
    var text = $(this).val();
    reFetch(text);
  })  
  $(".learnmore .link").click(function () {
    $(".learnmore p").fadeIn();
    $(this).hide();
  });
});

function reFetch(query) {
  $.ajax({
    url: "search?query="+query,
    success: function(msg) {
      changeResult(msg);
    },
    fail: function(msg) {
      alert(msg);
    }
  }); 
}

function changeResult(msg) {
  // for illustration.
  $('.raw').show();
  $('.rawResponse').html(JSON.stringify(msg));
  
  // remove learn more text
  $('.learnmore p').hide();
  $('.learnmore .link').show();

  // decide which result heading to show
  if (msg.results.length == 0) {
    $('.resultHeader').hide();
    $('.noresults').fadeIn();
  } else {
    $('.resultHeader').show();
    $('.noresults').hide();
  }
  // display results in table
  var $prettyDiv = $('#prettyResponse');
  $($prettyDiv).html("");
  $.each(msg.results, function(index, result) {
    var resultClass = "result";
    if (index % 2 == 0) {
      resultClass += " even";
    } else {
      resultClass += " odd";
    }
    var $newRow  = $("<div class='" + resultClass + "'><div class='city'>" + result.name + "</div><div class='score'> " + result.score + "</div></div>");
    $($prettyDiv).append($newRow); 
  });
}
 
