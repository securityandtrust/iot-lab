/**
 * Created by gregory.nain on 06/12/2013.
 */



var DemoDeployer = function(){

    var init = function() {
        $(".iotdemo").each(function() {
            $(this).on("click",function(){
                console.debug("Clicked on " + this);
               $.post( "/deploy", {targetdemo:$(this).attr('data-target')})
                    .done(function() {
                       console.debug("Deployed OK");
                    })
                    .fail(function() {
                       console.debug("Deployed FAILED");
                    })
            });
        });
    };

    return {
        init : init()
    }

}();

