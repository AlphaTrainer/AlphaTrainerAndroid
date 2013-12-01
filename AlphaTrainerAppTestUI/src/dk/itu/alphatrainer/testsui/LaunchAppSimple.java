package dk.itu.alphatrainer.testsui;

//Import the uiautomator libraries
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;


/*
 * From:
 * - http://www.vogella.com/articles/AndroidTesting/article.html
 * - http://developer.android.com/tools/testing/testing_ui.html
 * - ...
 * 
 * launch the viewer for ui inspections / selectors CLI: 
 * $ uiautomatorviewer
 * 
 */
public class LaunchAppSimple extends UiAutomatorTestCase {

public void testDemo() throws UiObjectNotFoundException {

 // Simulate a short press on the HOME button.
 getUiDevice().pressHome();

 // We’re now in the home screen. Next, we want to simulate
 // a user bringing up the All Apps screen.
 // If you use the uiautomatorviewer tool to capture a snapshot
 // of the Home screen, notice that the All Apps button’s
 // content-description property has the value “Apps”. We can
 // use this property to create a UiSelector to find the button.
 UiObject allAppsButton = new UiObject(new UiSelector().description("Apps"));

 // Simulate a click to bring up the All Apps screen.
 allAppsButton.clickAndWaitForNewWindow();

 // Select AlphaTrainerApp
 UiObject brainAppIcon = new UiObject(new UiSelector().text("Alpha Trainer"));

 // Simulate a click to enter the Apps tab.
 brainAppIcon.click();


 // Validate that the package name is the expected one
 UiObject brainappValidation = new UiObject(new UiSelector()
     .packageName("dk.itu.alphatrainer"));
 assertTrue("Start screen of the alpha trainer", brainappValidation.exists());
}
} 