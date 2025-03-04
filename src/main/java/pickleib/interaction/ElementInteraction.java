package pickleib.interaction;

import context.ContextStore;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.remote.RemoteExecuteMethod;
import org.openqa.selenium.remote.html5.RemoteWebStorage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pickleib.utilities.ScreenCaptureUtility;
import pickleib.utilities.WebUtilities;
import records.Bundle;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.StringUtilities.Color.*;

@SuppressWarnings("unused")
public class ElementInteraction extends WebUtilities {

    private final ScreenCaptureUtility capture = new ScreenCaptureUtility();

    /**
     *
     * Navigate to url: {url}
     *
     * @param url target url
     */
    public void getUrl(String url) {
        url = strUtils.contextCheck(url);
        driver.get(url);
    }

    /**
     *
     * Go to the {page} page
     *
     * @param page target page
     */
    public void toPage(String page){
        String url = driver.getCurrentUrl();
        String pageUrl = url + page;
        navigate(pageUrl);
    }

    /**
     *
     * Switch to the next tab
     *
     */
    public void switchToNextTab() {
        String parentHandle = switchWindowByHandle(null);
        ContextStore.put("parentHandle", parentHandle);
    }

    /**
     *
     * Switch to a specified parent tab
     *
     */
    public void switchToParentTab() {
        switchWindowByHandle(ContextStore.get("parentHandle").toString());
    }

    /**
     *
     * Switch to the tab with handle: {handle}
     * Switches a specified tab by tab handle
     *
     * @param handle target tab handle
     */
    public void switchToTabByHandle(String handle) {
        handle = strUtils.contextCheck(handle);
        String parentHandle = switchWindowByHandle(handle);
        ContextStore.put("parentHandle", parentHandle);
    }

    /**
     *
     * Switch to the tab number {tab index}
     * Switches tab by index
     *
     * @param handle target tab index
     */
    public void switchToTabByIndex(Integer handle) {
        String parentHandle = switchWindowByIndex(handle);
        ContextStore.put("parentHandle", parentHandle);
    }

    /**
     * Get HTML at {htmlPath}
     * Acquires the HTML from a given directory
     *
     * @param htmlPath target directory
     */
    public void getHTML(String htmlPath) {
        htmlPath = strUtils.contextCheck(htmlPath);
        log.new Info("Navigating to the email @" + htmlPath);
        driver.get(htmlPath);
    }

    /**
     *
     * Set window width and height as {width} and {height}
     *
     * @param width target width
     * @param height target height
     */
    public void setFrameSize(Integer width, Integer height) {setWindowSize(width,height);}

    /**
     *
     * Adds given values to the local storage
     *
     * @param form Map(String, String)
     */
    public void addLocalStorageValues(Map<String, String> form){
        for (String valueKey: form.keySet()) {
            RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
            RemoteWebStorage webStorage = new RemoteWebStorage(executeMethod);
            LocalStorage storage = webStorage.getLocalStorage();
            storage.setItem(valueKey, strUtils.contextCheck(form.get(valueKey)));
        }
    }

    /**
     *
     * Adds given cookies
     *
     * @param cookies Map(String, String)
     */
    public void addCookies(Map<String, String> cookies){
        for (String cookieName: cookies.keySet()) {
            Cookie cookie = new Cookie(cookieName, strUtils.contextCheck(cookies.get(cookieName)));
            driver.manage().addCookie(cookie);
        }
    }

    /**
     * Refreshes the page
     */
    public void refresh() {refreshThePage();}

    /**
     * Deletes all cookies
     */
    public void deleteCookies() {driver.manage().deleteAllCookies();}

    /**
     *
     * Navigate browser in {direction} direction
     *
     * @param direction target direction (backwards or forwards)
     */
    public void browserNavigate(Navigation direction) {navigateBrowser(direction);}

    /**
     *
     * Click button with {text} text
     *
     * @param text target text
     */
    public void clickByText(String text) {
        clickButtonByText(text, true);}

    /**
     *
     * Click button includes {button text} text with css locator
     *
     * @param buttonText target text
     */
    public void clickByCssSelector(String buttonText) {
        WebElement element = driver.findElement(By.cssSelector(buttonText));
        clickElement(element, true);
    }

    /**
     *
     * Wait for {duration} seconds
     *
     * @param duration desired duration
     */
    public void wait(Integer duration) {
        waitFor(duration);
    }

    /**
     *
     * Scroll {direction}
     *
     * @param direction target direction (up or down)
     */
    public void scrollInDirection(Direction direction){scroll(direction);}

    /**
     *
     * Click the {button name} on the {page name}
     *
     * @param buttonName target button name
     * @param pageName specified page instance name
     */
    public void clickStep(WebElement button, String buttonName, String pageName){
        log.new Info("Clicking " +
                highlighted(BLUE, buttonName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        clickElement(centerElement(button));
    }

    /**
     *
     * Acquire the {attribute name} attribute of {element name} on the {page name}
     * (Use 'innerHTML' attributeName to acquire text on an element)
     *
     * @param attributeName acquired attribute name
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void saveAttributeValue(String attributeName, WebElement element, String elementName, String pageName){
        log.new Info("Acquiring " +
                highlighted(BLUE,attributeName) +
                highlighted(GRAY," attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        String attribute = element.getAttribute(attributeName);
        log.new Info("Attribute -> " + highlighted(BLUE, attributeName) + highlighted(GRAY," : ") + highlighted(BLUE, attribute));
        ContextStore.put(elementName + "-" + attributeName, attribute);
        log.new Info("Attribute saved to the ContextStore as -> '" +
                highlighted(BLUE, elementName + "-" + attributeName) +
                highlighted(GRAY, "' : '") +
                highlighted(BLUE, attribute) +
                highlighted(GRAY, "'")
        );
    }

    /**
     *
     * Acquire attribute {attribute name} from element {element name} on the {page name}
     * (Use 'innerHTML' attributeName to acquire text on an element)
     *
     * @param element target element
     * @param attributeName acquired attribute name
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void saveAttributeValue(
            WebElement element,
            String attributeName,
            String elementName,
            String pageName){
        log.new Info("Acquiring " +
                highlighted(BLUE,attributeName) +
                highlighted(GRAY," attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );

        String attribute = element.getAttribute(attributeName);
        log.new Info("Attribute -> " + highlighted(BLUE, attributeName) + highlighted(GRAY," : ") + highlighted(BLUE, attribute));
        ContextStore.put(elementName + "-" + attributeName, attribute);
        log.new Info("Attribute saved to the ContextStore as -> '" +
                highlighted(BLUE, elementName + "-" + attributeName) +
                highlighted(GRAY, "' : '") +
                highlighted(BLUE, attribute) +
                highlighted(GRAY, "'")
        );
    }

    /**
     *
     * Center the {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void center(WebElement element, String elementName, String pageName){
        log.new Info("Clicking " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        centerElement(element);
    }

    /**
     *
     * Click towards to {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void clickTowards(WebElement element, String elementName, String pageName){
        log.new Info("Clicking " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );

        clickAtAnOffset(element, 0, 0, false);
    }

    /**
     *
     * Perform a JS click on element {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void performJSClick(WebElement element, String elementName, String pageName){
        log.new Info("Clicking " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        clickWithJS(centerElement(element));
    }

    /**
     *
     * If present, click element {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void clickIfPresent(WebElement element, String elementName, String pageName){
        log.new Info("Clicking " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, ", if present...")
        );
        try {
            if (elementIs(element, ElementState.displayed)) clickElement(element, true);
        }
        catch (WebDriverException ignored){log.new Warning("The " + elementName + " was not present");}
    }

    /**
     *
     * Fill input element {input name} from {pageName} with text: {input text}
     *
     * @param inputElement target input element
     * @param inputName target input element name
     * @param pageName specified page instance name
     * @param input input text
     */
    public void basicFill(WebElement inputElement, String inputName, String pageName, String input){
        input = strUtils.contextCheck(input);
        log.new Info("Filling " +
                highlighted(BLUE, inputName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " with the text: ") +
                highlighted(BLUE, input)
        );
        clearFillInput(
                inputElement, //Element
                input, //Input Text
                false,
                true
        );
    }

    /**
     *
     * Fill form input on the {page name}
     *
     * @param bundles list of bundles where input element, input name and input texts are stored
     * @param pageName specified page instance name
     */
    public void fillForm(List<Bundle<WebElement, String, String>> bundles, String pageName){
        String inputName;
        String input;
        for (Bundle<WebElement, String, String> bundle : bundles) {
            log.new Info("Filling " +
                    highlighted(BLUE, bundle.theta()) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, bundle.beta())
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            clearFillInput(bundle.alpha(), //Input Element
                    bundle.beta(), //Input Text
                    false,
                    true
            );
        }
    }

    /**
     *
     * Fill iFrame element {element name} of {iframe name} on the {page name} with text: {input text}
     *
     * @param iframe target iframe
     * @param element target element
     * @param inputName target element name
     * @param pageName specified page instance name
     * @param inputText input text
     */
    public void fillIframeInput(
            WebElement iframe,
            WebElement element,
            String inputName,
            String pageName,
            String inputText){
        inputText = strUtils.contextCheck(inputText);
        log.new Info("Filling " +
                highlighted(BLUE, inputName) +
                highlighted(GRAY," i-frame element input on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " with the text: ") +
                highlighted(BLUE, inputText)
        );
        elementIs(iframe, ElementState.displayed);
        driver.switchTo().frame(iframe);
        clearFillInput(element, inputText,true,true);
        driver.switchTo().parentFrame();
    }

    /**
     *
     * Click iFrame element {element name} in {iframe name} on the {page name}
     *
     * @param iframe target iframe
     * @param element target element
     * @param elementName target element name
     * @param iframeName target iframe name
     * @param pageName specified page instance name
     */
    public void clickIframeElement(WebElement iframe, WebElement element, String elementName, String iframeName, String pageName){
        log.new Info("Clicking the i-frame element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        driver.switchTo().frame(iframe);
        click(element);
    }

    /**
     *
     * Fill {iframe name} iframe form input on the {page name}
     *
     * @param bundles list of bundles where input element, input name and input texts are stored
     * @param element target element
     * @param iframeName target iframe name
     * @param pageName specified page instance name
     * @param forms table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
     */
    public void fillFormIframe(
            List<Bundle<WebElement, String, String>> bundles,
            WebElement element,
            String iframeName,
            String pageName,
            List<Map<String, String>> forms){
        String inputName;
        String input;
        for (Bundle<WebElement, String, String> bundle : bundles) {
            log.new Info("Filling " +
                    highlighted(BLUE, bundle.theta()) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, bundle.beta())
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            driver.switchTo().frame(element);

            clearFillInput(
                    bundle.alpha(),
                    bundle.beta(),
                    false,
                    true
            );
        }
    }

    /**
     *
     * Verify the text of {element name} on the {page name} to be: {expected text}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param expectedText expected text
     */
    public void verifyText(WebElement element, String elementName, String pageName, String expectedText){
        expectedText = strUtils.contextCheck(expectedText);
        log.new Info("Performing text verification for " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        Assert.assertEquals(expectedText, element.getText());
        log.new Success("Text of the element " + elementName + " was verified!");
    }

    /**
     *
     * Verify the text of {element name} on the {page name} to be: {expected text}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param expectedText expected text
     */
    public void verifyContainsText(WebElement element, String elementName, String pageName, String expectedText){
        expectedText = strUtils.contextCheck(expectedText);
        log.new Info("Performing text verification for " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        Assert.assertTrue(element.getText().contains(expectedText));
        log.new Success("Text of the element " + elementName + " was verified!");
    }

    /**
     *
     * Verify text of element from list on the {page name}
     *
     * @param pageName specified page instance name
     */
    public void verifyListedText(
            List<Bundle<WebElement, String, String>> bundles,
            String pageName){
        for (Bundle<WebElement, String, String> bundle : bundles) {
            String elementName = bundle.beta();
            String expectedText = bundle.theta();
            log.new Info("Performing text verification for " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, expectedText)
            );
            Assert.assertEquals("The " + bundle.alpha().getText() + " does not contain text '", expectedText, bundle.alpha().getText());
            log.new Success("Text of the element" + bundle.alpha().getText() + " was verified!");

        }
    }

    /**
     *
     * Verify presence of element {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void verifyPresence(WebElement element, String elementName, String pageName){
        log.new Info("Verifying presence of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        verifyElementState(element, ElementState.displayed);
        log.new Success("Presence of the element " + elementName + " was verified!");
    }

    /**
     * Closes the browser
     */
    public void closeBrowser(){
        driver.quit();
    }

    /**
     *
     * Verify that the element {element name} on the {page name} is in {expected state} state
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param expectedState expected state
     */
    public void verifyState(
            WebElement element,
            String elementName,
            String pageName,
            ElementState expectedState){
        log.new Info("Verifying " +
                highlighted(BLUE, expectedState.name()) +
                highlighted(GRAY," state of ")+
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        verifyElementState(element, expectedState);
        log.new Success("The element " + elementName + " was verified to be " + expectedState.name());
    }

    /**
     *
     * Wait for absence of element {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void waitUntilAbsence(WebElement element, String elementName, String pageName){
        log.new Info("Waiting for the absence of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        elementIs(element, ElementState.absent);
    }

    /**
     *
     * Wait for element {element name} on the {page name} to be visible
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void waitUntilVisible(WebElement element, String elementName, String pageName) {
        log.new Info("Waiting for the absence of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        elementIs(element, ElementState.displayed);
    }

    /**
     *
     * Wait until element {element name} on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param attributeValue expected attribute value
     * @param attributeName target attribute name
     */
    public void waitUntilElementContainsAttribute(
            WebElement element,
            String elementName,
            String pageName,
            String attributeName,
            String attributeValue) {
        log.new Info("Waiting for the absence of " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        try {wait.until((ExpectedConditions.attributeContains(element, attributeName, attributeValue)));}
        catch (WebDriverException ignored) {}
    }

    /**
     *
     * Verify that element {element name} on the {page name} has {attribute value} value for its {attribute name} attribute
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     * @param attributeValue expected attribute value
     * @param attributeName target attribute name
     */
    public void verifyElementContainsAttribute(
            WebElement element,
            String elementName,
            String pageName,
            String attributeName,
            String attributeValue) {

        log.new Info("Verifying " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        Assert.assertTrue(
                "The " + attributeName + " attribute of element " + elementName + " could not be verified." +
                        "\nExpected value: " + attributeValue + "\nActual value: " + element.getAttribute(attributeName),
                wait.until(ExpectedConditions.attributeContains(element, attributeName, attributeValue))
        );
        log.new Success("Value of '" + attributeName + "' attribute is verified to be '" + attributeValue + "'!");
    }

    /**
     *
     * Verify {attribute name} css attribute of element {element name} on the {page name} is {attribute value}
     *
     * @param element target element
     * @param attributeName target attribute name
     * @param elementName target attribute name
     * @param pageName specified page instance name
     * @param attributeValue expected attribute value
     */
    public void verifyElementColor(
            WebElement element,
            String attributeName,
            String elementName,
            String pageName,
            String attributeValue) {

        log.new Info("Verifying " +
                highlighted(BLUE, attributeName) +
                highlighted(GRAY, " attribute of ") +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName)
        );
        Assert.assertEquals(
                "The " + attributeName + " attribute of element " + elementName + " could not be verified." +
                        "\nExpected value: " + attributeValue + "\nActual value: " + element.getCssValue(attributeName),
                attributeValue
        );
        log.new Success("Value of '" + attributeName + "' attribute is verified to be '" + attributeValue + "'!");
    }

    /**
     *
     * Verify presence of listed element from list on the {page name}
     *
     * @param bundles list that contains element, elementName, elementText
     * @param pageName specified page instance name
     * @param signForms table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
     */
    public void verifyPresenceOfListedElements(
            List<Bundle<WebElement, String, String>> bundles,
            WebElement element,
            List<WebElement> elements,
            String pageName,
            List<Map<String, String>> signForms){

        for (Bundle<WebElement, String, String> bundle : bundles) {
            String elementName = bundle.beta();
            String expectedText = strUtils.contextCheck(bundle.theta());

            log.new Info("Performing text verification for " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, pageName) +
                    highlighted(GRAY, " with the text: ") +
                    highlighted(BLUE, expectedText)
            );
            Assert.assertTrue(
                    "The " + elementName + " does not contain text '" + expectedText + "' ",
                    element.getText().contains(expectedText)
            );
            log.new Success("Text of '" + elementName + "' verified as '" + expectedText + "'!");
        }
    }

    /**
     *
     * Verify the page is redirecting to the page {target url}
     *
     * @param url target url
     */
    public void verifyCurrentUrl(String url) {
        url = strUtils.contextCheck(url);
        log.new Info("The url contains " + url);
        Assert.assertTrue("Current url does not contain the expected url!", driver.getCurrentUrl().contains(url));
    }

    /**
     *
     * Click on a button that contains {button text} text
     *
     * @param buttonText target button text
     * @param scroll scrolls if true
     */
    public void clickButtonByText(String buttonText, Boolean scroll) {
        this.clickElement(this.getElementByText(buttonText), scroll);
    }

    /**
     *
     * Update context {key} -> {value}
     *
     * @param key Context key
     * @param value Context value
     */
    public void updateContext(String key, String value){
        value = strUtils.contextCheck(value);
        log.new Info(
                "Updating context: " +
                        highlighted(BLUE, key) +
                        highlighted(GRAY, " -> ") +
                        highlighted(BLUE, value)
        );
        ContextStore.put(key, value);
    }

    /**
     *
     * Press {target key} key on {element name} element of the {}
     *
     * @param key target key
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void pressKey(WebElement element, Keys key, String elementName, String pageName){
        log.new Info("Filling the giving input " + elementName + " with " + key );
        element.sendKeys(key);
    }

    /**
     *
     * Execute JS command: {script}
     *
     * @param script JS script
     */
    //@Given("Execute JS command: {}")
    public void executeJSCommand(String script) {
        executeScript(script);
    }

    /**
     *
     * Listen to {event name} event and print {specified script} object
     * example -> listenerScript = "_ddm.listen(" + eventName + ");";
     *
     * @param eventName target event name
     * @param objectScript object script
     */
    //@Given("Listen to {} event & print {} object")
    public void listenGetAndPrintObject(String listenerScript, String eventName, String objectScript)  {
        objectScript = "return " + objectScript;
        if (isEventFired(eventName, listenerScript)) {
            Object object = executeScript(objectScript);
            log.new Info(object.toString());
        }
    }

    /**
     *
     * Upload file on input {input element field name} on the {page name} with file: {target file path}
     *
     * @param inputElement target input element
     * @param inputName input element field name
     * @param pageName specified page instance name
     * @param absoluteFilePath target file path
     */
    public void fillInputWithFile(
            WebElement inputElement,
            String inputName,
            String pageName,
            String absoluteFilePath){
        absoluteFilePath = strUtils.contextCheck(absoluteFilePath);
        log.new Info("Filling " +
                highlighted(BLUE, inputName) +
                highlighted(GRAY," on the ") +
                highlighted(BLUE, pageName) +
                highlighted(GRAY, " with the text: ") +
                highlighted(BLUE, absoluteFilePath)
        );
        clearFillInput(
                inputElement,
                absoluteFilePath,
                false,
                false
        );
    }

    /**
     *
     * Listen to {event name} event and verify value of {node source} node is {expected value}
     * example -> listenerScript = "_ddm.listen(" + eventName + ");";
     *
     * @param eventName evet name
     * @param nodeSource node source
     * @param expectedValue expected value
     */
    public void listenGetAndVerifyObject(String listenerScript, String eventName, String nodeSource, String expectedValue)  {
        log.new Info("Verifying value of '" + nodeSource + "' node");
        String nodeScript = "return " + nodeSource;
        if (isEventFired(eventName, listenerScript)) {
            Object object = executeScript(nodeScript);
            Pattern sourcePattern = Pattern.compile(expectedValue);
            Matcher nodeValueMatcher = sourcePattern.matcher(object.toString());
            Assert.assertTrue("Node values do not match! Expected: " + object + ", Found: " + object, nodeValueMatcher.find());
            log.new Success("Value of '" + nodeSource + "' is verified to be '" + object + "'");
        }
        else log.new Warning("'" + eventName + "' event is not fired!");
    }

    /**
     *
     * Listen to {event name} event and verify values of the following nodes
     * example -> listenerScript = "_ddm.listen(" + eventName + ");";
     *
     * @param eventName event name
     * @param nodeList target node list
     */
    public void listenGetAndVerifyObject(String listenerScript, String eventName,  List<Map<String, String>> nodeList)  {
        if (isEventFired(eventName, listenerScript)) {
            for (Map<String, String> nodeMap:nodeList) {
                String nodeSource = nodeMap.get("Node Source");
                String nodeValue = nodeMap.get("Node Value");

                log.new Info("Verifying value of '" + highlighted(BLUE, nodeSource) + highlighted(GRAY, "' node"));
                String nodeScript = "return " + nodeSource;
                Object object = executeScript(nodeScript);

                Pattern sourcePattern = Pattern.compile(nodeValue);
                Matcher nodeValueMatcher = sourcePattern.matcher(object.toString());

                Assert.assertTrue("Node values do not match! Expected: " + nodeValue + ", Found: " + object, nodeValueMatcher.find());
                log.new Success("Value of '" + nodeSource + "' is verified to be '" + object + "'");
            }
        }
        else throw new RuntimeException("'" + eventName + "' event is not fired!");
    }

}
