package com.dantegarden.updatehosts.service;

import javafx.scene.control.ProgressBar;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: lij
 * @create: 2020-02-10 21:25
 */
@Component
public class ProgressHolder {
    private ProgressBar progressBar;
    private Integer stepNum;
    private Float stepSize;

    static class ProgressTask implements Runnable {

        ProgressBar progressBar;
        double value;

        public ProgressTask(ProgressBar progressBar, double value) {
            this.progressBar = progressBar;
            this.value = value;
        }

        @Override
        public void run() {
            this.progressBar.setProgress(value);
            System.out.println(Thread.currentThread().getName() + "run progress " + value);
        }
    }

    public void init(ProgressBar progressBar){
        this.progressBar = progressBar;
        this.stepNum = 0;
        this.stepSize = 0f;
        new Thread(new ProgressTask(this.progressBar, 0.0)).start();
    }

    public void start(Integer stepNum){
        if(this.progressBar!=null){
            this.stepNum = stepNum;
            this.stepSize = (float)1/stepNum;
            new Thread(new ProgressTask(this.progressBar, 0.0)).start();
        }
    }

    public void go(){
        if(this.progressBar!=null && this.stepNum!=0){
            this.stepNum--;
            double value = 0;
            if(this.stepNum == 0){
                value = 1.0;
            }else{
                value = 1.0 - this.stepNum * this.stepSize;
            }
            new Thread(new ProgressTask(this.progressBar, value)).start();
        }
    }

    public void finish(){
        if(this.progressBar!=null){
            new Thread(new ProgressTask(this.progressBar, 1.0)).start();
        }
    }
}
