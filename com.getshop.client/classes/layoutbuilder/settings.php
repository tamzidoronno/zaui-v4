
<div>
    <label><? echo $this->factory->__w("Display this cell when logged on (it will always be visible for administrators)"); ?>
        <span class='gscssinput'>
            <input type='checkbox' gsname='displayWhenLoggedOn'> 
        </span>
    </label>
</div>
<div style='clear:both;'></div>

<br>
<div>
    <label><? echo $this->factory->__w("Hide this cell on a cell phone"); ?>
        <span class='gscssinput'>
            <input type='checkbox' gsname='hideOnMobile'> 
        </span>
    </label>
</div>
<div style='clear:both;'></div>
<br>
<div>
    <label> <? echo $this->factory->__w("Keep original layout in mobile view."); ?>
        <span class='gscssinput'>
            <input type="checkbox" class='gskeepOriginalLayout'>
        </span>
    </label>
</div>
<div style='clear:both;'></div>
<br>
<div>
    <label><? echo $this->factory->__w("Display this cell when logged out"); ?>
        <span class='gscssinput'>
            <input type='checkbox' gsname='displayWhenLoggedOut'> 
        </span>
    </label>
</div>
<div style='clear:both;'></div>
<br>
<div>
    <label>
        <? echo $this->factory->__w("User level for cell"); ?>

        <span class='gscssinput'>
            <select gsname='editorLevel'>
                <option value='0'>Everyone</option>
                <option value='10'>Users</option>
                <option value='50'>Editors</option>
                <option value='100'>Administrators</option>
            </select>
        </span>
    </label>
</div>
<div style='clear:both;'></div>
<br>
<div>
    <label><? echo $this->factory->__w("Link this cell"); ?>
        <span class='gscssinput'>
            <input type='txt' gsname='link'> 
        </span>
    </label>
</div>
<div style='clear:both;'></div>
<br>
<div>
    <label> <? echo $this->factory->__w("Anchor"); ?>
        <span class='gscssinput'>
                <input id="gs_settings_cell_anchor" type='text'/>
        </span>
    </label>
</div>
<div style='clear:both;'></div>
