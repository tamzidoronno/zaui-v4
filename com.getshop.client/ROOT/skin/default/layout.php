root { 
    display: block;
}

body {
}

body,table,td,th  {
    margin: 0px;
    font-size: 13px;
    font:11px/1.55 Tahoma, Arial, Helvetica, sans-serif; 
}

body {
    overflow-y:scroll;
}

.header,
.footer,
.breadcrumb,
.subheader,
.mainarea {
    margin: 0 auto;
    border: 0px;
}

.skeleton1 .mainarea .left { width: <? echo $this->getWidthLeft() - 4;?>px; }
.skeleton1 .mainarea .middle { width: <? echo $this->getWidthMidle() - 4;?>px; }
.skeleton1 .mainarea .right { width: <? echo $this->getWidthRight() - 4;?>px; }

.skeleton2 .mainarea .left { width: <? echo $this->getWidthLeft() - 4;?>px; }
.skeleton2 .mainarea .middle { width: <? echo ($this->getWidthLeft() + $this->getWidthMidle()) - 4;?>px; }

.skeleton3 .mainarea .middle { width: <? echo ($this->getWidthRight() + $this->getWidthMidle()) - 4;?>px; }
.skeleton3 .mainarea .right { width: <? echo $this->getWidthLeft() - 4;?>px; }

.skeleton4 .mainarea .middle { width: <? echo $this->getWidthTotal();?>px; }

.skeleton5 .mainarea { width: 100%; }
.skeleton5 .mainarea .middle { width: 100%; }

.skeleton6 .mainarea .middle { width: <? echo $this->getWidthTotal();?>px; }
.skeleton6 .subheader-outer .subheader { min-height: 80px; width: <? echo $this->getWidthTotal();?>px; }

.header { height: <? echo $this->getHeaderHeight();?>px; }
.footer-outer { width: 100% }

.left,
.middle,
.right { padding-bottom: 10000px; margin-bottom: -10000px; min-height: 400px; overflow: auto; } 
