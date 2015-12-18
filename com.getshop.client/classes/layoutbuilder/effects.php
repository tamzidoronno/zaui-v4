
<?
if (!$this->factory->isEffectsEnabled()) {
    echo $this->factory->__f("Effects disabled, this module requires a higher SLA.");
} else {
    ?>
    <h2>Scroll effect - Fadein</h2>
    <div>
        <label><? echo $this->factory->__w("Flip contect to add content underneat this cell"); ?>
            <span class='gscssinput'>
                <input type='checkbox' gsname='isFlipping'> 
            </span>
        </label>
    </div>
    <div style='clear:both;'></div>
    <div>
        <label><? echo $this->factory->__w("Fade in when scrolling"); ?>
            <span class='gscssinput'>
                <input type='checkbox' gsname='scrollFadeIn'> 
            </span>
        </label>
    </div>
    <div style='clear:both;'></div>
    <br>
    <div>
        <label><? echo $this->factory->__w("Start opacity"); ?>
            <span class='gscssinput'>
                <input type='textfield' gsname='scrollFadeInStartOpacity'> 
            </span>
        </label>
    </div>
    <div style='clear:both;'></div>
    <br>
    <div>
        <label><? echo $this->factory->__w("End opacity"); ?>
            <span class='gscssinput'>
                <input type='textfield' gsname='scrollFadeInEndOpacity'> 
            </span>
        </label>
    </div>
    <div style='clear:both;'></div>
    <br>
    <div>
        <label><? echo $this->factory->__w("Duraction (ms)"); ?>
            <span class='gscssinput'>
                <input type='textfield' gsname='scrollFadeInDuration'> 
            </span>
        </label>
    </div>
    <div style='clear:both;'></div>
    <br>
    <div>
        <label><? echo $this->factory->__w("Slide left (px) (negative is from right)"); ?>
            <span class='gscssinput'>
                <input type='textfield' gsname='slideLeft'> 
            </span>
        </label>
    </div>
    <div style='clear:both;'></div>
    <br>
    <div>
        <label><? echo $this->factory->__w("Slide top (px) (negative is from bottom)"); ?>
            <span class='gscssinput'>
                <input type='textfield' gsname='slideTop'> 
            </span>
        </label>
    </div>
    <br/>
    <br/>
    <h2>Scroll effect - Parallex (works best on outer row)</h2>
    <div>
        <label><? echo $this->factory->__w("If you enable this, the parallex effect will be enabled. The background will scroll faster then the cell on top"); ?>
            <span class='gscssinput'>
                <input type='checkbox' gsname='paralexxRow'> 
            </span>
        </label>
    </div>
    <?
}
?>