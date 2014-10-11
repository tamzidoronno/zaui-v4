<?php

class Page {

    var $javapage;
    /*  @var $factory Factory */
    var $factory;

    /**
     * 
     * @param type $javapage
     * @param Factory $factory
     */
    function __construct($javapage, $factory) {
        $this->javapage = $javapage;
        $this->factory = $factory;
    }

    function getApplications() {
        return array();
    }

    function getId() {
        return $this->javapage->id;
    }

    function loadSkeleton() {
        /* @var $layout core_pagemanager_data_PageLayout */
        $layout = $this->javapage->layout;

        echo "<span class='gscellsettingspanel'>";
        echo "<div class='gs_splithorizontally' type='split_horizontal'><i class='fa fa-chevron-circle-right'> " . $this->factory->__w("Add row") . "</i></div>";
        echo "<div class='gs_splitvertically' type='split_vertical'><i class='fa fa-chevron-circle-down'> " . $this->factory->__w("Add column") . "</i></div>";
        echo "<div class='gs_removerow' type='delete'><i class='fa fa-trash-o'> " . $this->factory->__w("Delete") . "</i></div>";
        echo "</span>";

        if ($layout->header) {
            $this->printCell($layout->header, 0, 0, 0, false);
        }
        $count = 0;

        $_SESSION['gseditcell'] = "737f002c-bd3a-44bd-ba1d-f7745318b003";


        foreach ($layout->rows as $row) {
            if (isset($_SESSION['gseditcell']) && $_SESSION['gseditcell'] === $row->cellId) {
                $this->printCell($row, $count, 0, 0, true);
            } else {
                $this->printCell($row, $count, 0, 0, false);
            }
            $count++;
        }
        echo '<div class="gs_addcell" incell="" aftercell="" style="padding: 20px; text-align:center"><span style="border: solid 1px; padding: 10px; background-color:#BBB;">Add row</span></div>';

        if ($layout->footer) {
            $this->printCell($layout->footer, 0, 0, 0, false);
        }
    }
	
	private function printApplicationAddCellRow($cell) {
		echo "<div class='gs_add_applicationlist'>";
		$apps = $this->factory->getApi()->getStoreApplicationPool()->getApplications();
		foreach ($apps as $app) {
			$name = $app->appName;
			$id = $app->id;
			echo "<div class='gs_add_app_entry' appId='$id'>$name</div>";
		}
		echo "</div>";
	}
	
	private function renderApplication($cell) {
		$instance = $this->factory->getApplicationPool()->getApplicationInstance($cell->appId);
		if ($instance) {
			$instance->renderApplication();
		}
	}

    function printCell($cell, $count, $depth, $totalcells, $edit) {
        $direction = "gshorisontal";
        if ($cell->vertical) {
            $direction = "gsvertical";
        }

        $rowedit = "";
        if ($edit) {
            $rowedit = "gseditrow";
        }

        echo "<div class='gscell $rowedit gsdepth_$depth gscount_$count $direction' cellid='" . $cell->cellId . "' totalcells='$totalcells'>";
        echo "<span class='gscellsettings'></span>";
        echo "<div class='gsinner gsdepth_$depth gscount_$count' totalcells='$totalcells'>";
        if (sizeof($cell->cells) > 0) {
            $innercount = 0;
            $innerdept = $depth + 1;
            foreach ($cell->cells as $innercell) {
                $this->printCell($innercell, $innercount, $innerdept, sizeof($cell->cells), $edit);
                $innercount++;
            }
        } else {
            if (!$cell->appId) {
                echo "<span class='gsaddcontent'>";
                echo "<i class='fa fa-plus-circle gs_show_application_add_list'></i>";
                echo "</span>";
				$this->printApplicationAddCellRow($cell);
            } else {
				$this->renderApplication($cell);
			}
        }
        echo "</div>";
        echo "</div>";
    }

}
