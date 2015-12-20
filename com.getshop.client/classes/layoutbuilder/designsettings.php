<?
$factory = IocContainer::getFactorySingelton();
?>
<table width='100%'>
    <tr>
        <td>
            <div class='gsoutercolorselectionpanel gsoutercolorselectionbg'>
                <div class='gsheading'><? echo $this->factory->__w("Outer background"); ?></div>
                <div class='gscolorselectionpanel' level=''>
                    <table width='100%'>
                        <tr>
                            <td valign="top">
                                <? echo $this->factory->__w("Select a background image."); ?>
                            </td>
                            <td align='right'>
                                <div class="inputWrapper">
                                    <span class='gsuploadimage' style='display:none;'><i class='fa fa-spin fa-spinner'></i></span>
                                    <input type='button' value='<? echo $this->factory->__w("Choose"); ?>' class='gschoosebgimagebutton'>
                                    <input class="fileInput gsbgimageselection" type="file" />
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </td>
        <td style='padding-left: 20px;'>
            <div class='gsoutercolorselectionpanel' >
                <div class='gsheading'><? echo $this->factory->__w("Inner background"); ?></div>
                <div class='gscolorselectionpanel' level='.gsuicell'>
                    <table width='100%'>
                        <tr>
                            <td valign="top">
                                <? echo $this->factory->__w("Select a background image."); ?>
                            </td>                                
                            <td align='right'>
                                <div class="inputWrapper">
                                    <span class='gsuploadimage' style='display:none;'><i class='fa fa-spin fa-spinner'></i></span>
                                    <input type='button' value='<? echo $this->factory->__w("Choose"); ?>' class='gschoosebgimagebutton'>
                                    <input class="fileInput gsbgimageselection" type="file" />
                                </div>
                            </td>
                        </tr>
                    </table>
                </div> 
            </div>
        </td>
    </tr>
</table>

<br>

<div class='gsheading'><? echo $this->factory->__w("Other attributes"); ?></div>

<div class='gscssattributes'>
    <div class="gscssrow">
            <span style='float:right;padding-right:67px; font-size: 10px; padding-top: 5px;'>Inner &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Outer</span>
            <br>
        <? echo $this->factory->__w("Background color"); ?> <span class="gscssinput">
            <span class='gspickerwrapper'>
                <input type='text' data-attr="background-color"  data-level='.gsuicell' class='gsinnerbgcolor gspickerinput' style='width:60px; padding:0px;'>
                <div class='gscolselector'>
                    <i class="fa fa-eyedropper"></i>
                </div>
            </span>
            <span class='gspickerwrapper'>
                <input type='text' data-attr="background-color"  data-level='.gsucell' class='gsouterbgcolor gspickerinput' style='width:60px; padding:0px;'>
                <div class='gscolselector'>
                    <div>
                        <i class="fa fa-eyedropper"></i>
                    </div>
                </div>
            </span>
        </span>
    </div>
    <div style='clear:both;'></div>
    <div class="gscssrow">
        <? echo $this->factory->__w("Mouse over background"); ?> <span class="gscssinput">
            <span class='gspickerwrapper'>
                <input type='text' data-attr="background-color"  data-level='.gsuicell:hover' class='gsinnerbgcolormouseover gspickerinput' style='width:60px; padding:0px;'>
                <div class='gscolselector'>
                    <div>
                        <i class="fa fa-eyedropper"></i>
                    </div>
                </div>
            </span>
            <span class='gspickerwrapper'>
                <input type='text' data-attr="background-color"  data-level='.gsucell:hover' class='gsouterbgcolormouseover gspickerinput' style='width:60px; padding:0px;'>
                <div class='gscolselector'>
                    <div>
                        <i class="fa fa-eyedropper"></i>
                    </div>
                </div>
            </span>
        </span>
    </div>
    <div style='clear:both;'></div>
    <div class="gscssrow">
        <? echo $this->factory->__w("Text color"); ?> <span class="gscssinput">
            <span class='gspickerwrapper'>
                <input type='text' data-attr="color" style='width:60px; padding:0px;' class='gspickerinput'>
                <div class='gscolselector'>
                    <div>
                        <i class="fa fa-eyedropper"></i>
                    </div>
                </div>
            </span>
        </span>
    </div>
    <div class="gscssrow">
        <? echo $this->factory->__w("Mouse over text color"); ?> <span class="gscssinput">
            <span class='gspickerwrapper'>
                <input type='text' data-attr="color" style='width:60px; padding:0px;' data-level='.gsucell:hover' class='gspickerinput'>
                <div class='gscolselector'>
                    <div>
                        <i class="fa fa-eyedropper"></i>
                    </div>
                </div>
            </span>
        </span>
    </div>
    <div style='clear:both;'></div>
    <div class="gscssrow">
        <? echo $this->factory->__w("Height"); ?> <span class="gscssinput"><input type='text' data-attr="min-height" data-prefix="px" data-level='.gsuicell'><span class='gsprefixbox'>px</span></span>
    </div>
    <div style='clear:both;'></div>
    <span style='float:right;padding-right:36px; font-size: 10px; padding-top: 5px;'>Inner - Outer</span>
    <div style='clear:both;'></div>
    <div class="gscssrow">
        <? echo $this->factory->__w("Color transition"); ?> 
        <span class="gscssinput">
            <input type='text' data-attr="transition" data-partoval-prefix="all " data-prefix="s ease" data-level='.gsuicell:hover'>
            <input type='text' data-attr="transition" data-partoval-prefix="all " data-prefix="s ease" data-level='.gsucell:hover'>
            <span class='gsprefixbox'>s</span>
        </span>
    </div>
    <div style='clear:both;'></div>
    <div class="gscssrow">
        <? echo $this->factory->__w("Left spacing"); ?> 
        <span class="gscssinput">
            <input type='text' data-attr="padding-left" data-prefix="px" data-level='.gsuicell'> 
            <input type='text' data-attr="padding-left" data-prefix="px"><span class='gsprefixbox'>px</span>
        </span>
    </div>
    <div style='clear:both;'></div>
    <div class="gscssrow">
        <? echo $this->factory->__w("Right spacing"); ?> 
        <span class="gscssinput">
            <input type='text' data-attr="padding-right" data-prefix="px" data-level='.gsuicell'> 
            <input type='text' data-attr="padding-right" data-prefix="px"><span class='gsprefixbox'>px</span>
        </span>
    </div>
    <div style='clear:both;'></div>
    <div class="gscssrow">
        <? echo $this->factory->__w("Top spacing"); ?> 
        <span class="gscssinput">
            <input type='text' data-attr="padding-top" data-prefix="px" data-level='.gsuicell'> 
            <input type='text' data-attr="padding-top" data-prefix="px"><span class='gsprefixbox'>px</span>
        </span>
    </div>
    <div style='clear:both;'></div>
    <div class="gscssrow">
        <? echo $this->factory->__w("Bottom spacing"); ?> 
        <span class="gscssinput">
            <input type='text' data-attr="padding-bottom" data-prefix="px" data-level='.gsuicell'> 
            <input type='text' data-attr="padding-bottom" data-prefix="px"><span class='gsprefixbox'>px</span>
        </span>
    </div>
    <div style='clear:both;'></div>
    
    <?
        $themeClasses = $factory->getApplicationPool()->getSelectedThemeAppInstance();
        if (method_exists($themeClasses, "getThemeClasses")) {
            $themeClasses = $themeClasses->getThemeClasses();
            if (count($themeClasses) > 0) {
    ?>
            <div style="border-top: solid 1px #DDD; margin-top: 20px; padding-top: 20px;" id="themeClasses">
                <div class="gscssrow">
                    <? echo $this->factory->__w("Use a predefined layout for cell"); ?> 
                    <span class="gscssinput">
                        <select id="gs_select_cell_theme_class">
                            <option id="none_selected" value="">-</option>
                        <?
                            foreach ($themeClasses as $themeClass) {
                                echo "<option value='$themeClass'>$themeClass</option>";
                            }
                        ?>
                        </select>
                    </span>
                </div>
            </div>
    <?
            }
        }
    ?>
</div>


<br/>
<br/>

<script>
    var gsonworkingonpicker = null;
    $('.gsresizingpanel').draggable({handle: ".heading"});
    $('.gscolselector').on('click', function () {
        gsonworkingonpicker = $(this);
    });
    $('.gscolselector').ColorPicker({ onChange: function (hsb, hex, rgb) {
            var field = gsonworkingonpicker.closest('.gspickerwrapper').find('.gspickerinput');
            field.val("#" + hex);
            field.ColorPickerHide();
            field.keyup();
        }});
</script>