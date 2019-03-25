<div id="messagebox" class="ok">
    <div class="inner">
        <div class="title"></div>
        <div class="icon inline"></div>
        <div class="description inline"></div>
        <div class="okbutton"></div>
    </div>
</div>

<div class="gsoverlay1 <? echo $showingModal; ?>">

    <div class="gsoverlayinner">
        <div class='gs_loading_spinner'><i class='fa fa-spin'></i></div>
        <div class='content'>
            <?
            if ($showingModal) {
                $modalPage = $pageFactory->getPage($_SESSION['gs_currently_showing_modal']);
                $modalPage->renderPage();
            }
            ?>
        </div>
    </div>
</div>

<div class="gsoverlay2">
    <div class="gsoverlayinner">
        <div class='gs_loading_spinner'><i class='fa fa-spin'></i></div>
        <div class='content'></div>
    </div>
</div>

<div class="gsoverlay3">
    <div class="gsoverlayinner">
        <div class='gs_loading_spinner'><i class='fa fa-spin'></i></div>
        <div class='content'></div>
    </div>
</div>