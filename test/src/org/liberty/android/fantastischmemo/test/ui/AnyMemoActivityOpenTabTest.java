package org.liberty.android.fantastischmemo.test.ui;

import org.liberty.android.fantastischmemo.R;

import org.liberty.android.fantastischmemo.ui.AnyMemo;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

import android.view.KeyEvent;

import android.widget.TabHost;

public class AnyMemoActivityOpenTabTest extends ActivityInstrumentationTestCase2<AnyMemo> {

    protected AnyMemo mActivity;

    public AnyMemoActivityOpenTabTest() {
        super("org.liberty.android.fantastischmemo", AnyMemo.class);
    }

    private Solo solo;

    private TabHost tabHost;

    public void setUp() throws Exception{
        mActivity = this.getActivity();
        tabHost = (TabHost)mActivity.findViewById(android.R.id.tabhost);
        solo = new Solo(getInstrumentation(), mActivity);
        UITestHelper uiTestHelper = new UITestHelper(getInstrumentation().getContext(), mActivity);
        uiTestHelper.clearPreferences();
        uiTestHelper.setUpFBPDatabase();

        if (solo.searchText("New version")) {
            solo.clickOnText(solo.getString(R.string.ok_text));
        }

        // GO to Open tab
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                tabHost.requestFocus();
            }
        });
        sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT);
    }

    public void testOpenTabPrevDir() {
        assertTrue(solo.searchText(".."));
        // Should see anymemo directory in thel list
        assertTrue(solo.searchText("anymemo"));
    }

    public void testActionListStudy() {
        solo.clickOnText(UITestHelper.SAMPLE_DB_NAME);
        solo.clickOnText(solo.getString(R.string.study_text));
        solo.waitForActivity("MemoScreen");
        solo.sleep(3000);
    }

    public void testActionListPrevEdit() {
        solo.clickOnText(UITestHelper.SAMPLE_DB_NAME);
        solo.clickOnText(solo.getString(R.string.edit_button_text));
        solo.waitForActivity("EditScreen");
        solo.sleep(3000);
    }

    public void testActionListCardList() {
        solo.clickOnText(UITestHelper.SAMPLE_DB_NAME);
        solo.clickOnText(solo.getString(R.string.list_mode_text));
        solo.waitForActivity("ListEditScreen");
        solo.sleep(3000);
    }

    public void testActionListCram() {
        solo.clickOnText(UITestHelper.SAMPLE_DB_NAME);
        solo.clickOnText(solo.getString(R.string.learn_ahead));
        solo.waitForActivity("MemoScreen");
        solo.sleep(3000);
    }

    public void testActionListSettings() {
        solo.clickOnText(UITestHelper.SAMPLE_DB_NAME);
        solo.clickOnText(solo.getString(R.string.settings_menu_text));
        solo.waitForActivity("SettingsScreen");
        solo.sleep(3000);
    }

    public void testActionListDelete() {
        solo.clickOnText(UITestHelper.SAMPLE_DB_NAME);
        solo.clickOnText(solo.getString(R.string.delete_text));
        solo.clickOnButton(solo.getString(R.string.cancel_text));
        assertTrue(solo.searchText(UITestHelper.SAMPLE_DB_NAME));
        solo.clickOnText(UITestHelper.SAMPLE_DB_NAME);
        solo.clickOnText(solo.getString(R.string.delete_text));
        solo.clickOnButton(solo.getString(R.string.delete_text));
        assertFalse(solo.searchText(UITestHelper.SAMPLE_DB_NAME));
    }

    public void testCreateNewDb() {
        solo.sendKey(Solo.MENU);
        solo.clickOnText(solo.getString(R.string.fb_create_db));
        // index 0 if only one available.
        solo.enterText(0, "123testdb");
        solo.clickOnText(solo.getString(R.string.ok_text));
        assertTrue(solo.searchText("123testdb.db"));

        // delete the db
        solo.clickOnText("123testdb.db");
        solo.clickOnText(solo.getString(R.string.delete_text));
        solo.clickOnButton(solo.getString(R.string.delete_text));
        assertFalse(solo.searchText("123testdb.db"));
    }




    public void tearDown() throws Exception {
        try {
            solo.finishOpenedActivities();
            solo.sleep(1000);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        super.tearDown();
    }

}
