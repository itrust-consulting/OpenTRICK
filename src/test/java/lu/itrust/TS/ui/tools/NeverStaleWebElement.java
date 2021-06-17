package lu.itrust.TS.ui.tools;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class NeverStaleWebElement implements WebElement {
	private final WebDriver driver;
	private WebElement element;
	private final By foundBy;

	public NeverStaleWebElement(WebDriver driver, By foundBy) {
		this.element = driver.findElement(foundBy);
		this.driver = driver;
		this.foundBy = foundBy;
	}

	@Override
	public void clear() {
		try {
			element.submit();
			element.clear();
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			clear();
		}
	}

	@Override
	public void click() {
			try {
				new BaseUnitTesting().click(foundBy);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	@Override
	public WebElement findElement(By by) {
		try {
			return element.findElement(by);
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			return element;
		} catch (Exception noSuchElementException) {
			return null;
		}
	}

	@Override
	public List<WebElement> findElements(By by) {
		try {
			return element.findElements(by);
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			return findElements(by);
		}
	}

	@Override
	public String getAttribute(String name) {
		try {
			return element.getAttribute(name);
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			return getAttribute(name);
		}
	}

	@Override
	public String getCssValue(String propertyName) {
		try {
			return element.getCssValue(propertyName);
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			return getCssValue(propertyName);
		}
	}

	public WebElement getElement() {
		try {
			return element;
		} catch (Exception e) {
			element = driver.findElement(foundBy);
			return element;
		}
	}

	public By getFoundBy() {
		return foundBy;
	}

	@Override
	public Point getLocation() {
		try {
			return element.getLocation();
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			return getLocation();
		}
	}

	@Override
	public Rectangle getRect() {
		try {
			return element.getRect();
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			return getRect();
		}
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		try {
			return element.getScreenshotAs(target);
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			return getScreenshotAs(target);
		}
	}

	@Override
	public Dimension getSize() {
		try {
			return element.getSize();
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			return getSize();
		}

	}

	@Override
	public String getTagName() {
		try {
			return element.getTagName();
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			return getTagName();
		}
	}

	@Override
	public String getText() {
		try {
			return element.getText();
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			return getText();
		}
	}

	@Override
	public boolean isDisplayed() {
		try {
			return element.isDisplayed();
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			return isDisplayed();
		}
	}

	@Override
	public boolean isEnabled() {
		try {
			return element.isEnabled();
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			return isEnabled();
		}
	}

	@Override
	public boolean isSelected() {
		try {
			return element.isSelected();
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			return isSelected();
		}
	}

	@Override
	public void sendKeys(CharSequence... keysToSend) {
		try {
			element.sendKeys(keysToSend);
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			sendKeys(keysToSend);
		}

	}

	@Override
	public void submit() {
		try {
			element.submit();
		} catch (StaleElementReferenceException e) {
			element = driver.findElement(foundBy);
			submit();
		}
	}

}