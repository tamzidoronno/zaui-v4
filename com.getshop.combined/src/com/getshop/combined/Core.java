package com.getshop.combined;

import com.thundashop.core.start.Runner;

public class Core extends Thread {
    private final String[] args;

    Core(String[] args) {
        this.args =args;
    }
    
    @Override
    public void run() {
        Runner runner = new Runner();
        try {
            runner.main(args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
