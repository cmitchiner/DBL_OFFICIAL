package com.example.messagingapp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.example.messagingapp.ChangePasswordActivity;

import com.google.firebase.database.core.view.Change;

import org.junit.Before;
import org.junit.Test;


public class ChangePasswordActivityTest {


    @Test
    public void checkPasswordFollowsFormat() {
        assertFalse(ChangePasswordActivity.passwordFollowsFormat("test"));
        assertTrue(ChangePasswordActivity.passwordFollowsFormat("Test123!"));
        assertFalse(ChangePasswordActivity.passwordFollowsFormat("Test123"));
        assertFalse(ChangePasswordActivity.passwordFollowsFormat("Te23!"));
        assertTrue(ChangePasswordActivity.passwordFollowsFormat("DBLapp123!"));
        assertFalse(ChangePasswordActivity.passwordFollowsFormat("Teeeeeeeeeeeeeeest123!"));
    }


}
