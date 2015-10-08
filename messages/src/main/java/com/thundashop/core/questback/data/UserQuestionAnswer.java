/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback.data;

import java.io.Serializable;

/**
 *
 * @author ktonder
 */
public class UserQuestionAnswer implements Serializable {
    public boolean hasAnsweredCorrectly = false;
    public boolean hasAnswered = false;
    public String questionId = "";
    public int tries = 0;
}
