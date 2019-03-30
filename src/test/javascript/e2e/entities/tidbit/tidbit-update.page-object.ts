import { element, by, ElementFinder } from 'protractor';

export default class TidbitUpdatePage {
  pageTitle: ElementFinder = element(by.id('tidbitsApp.tidbit.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  contentInput: ElementFinder = element(by.css('input#tidbit-content'));
  authorInput: ElementFinder = element(by.css('input#tidbit-author'));
  sourceInput: ElementFinder = element(by.css('input#tidbit-source'));
  urlInput: ElementFinder = element(by.css('input#tidbit-url'));
  categorySelect: ElementFinder = element(by.css('select#tidbit-category'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setContentInput(content) {
    await this.contentInput.sendKeys(content);
  }

  async getContentInput() {
    return this.contentInput.getAttribute('value');
  }

  async setAuthorInput(author) {
    await this.authorInput.sendKeys(author);
  }

  async getAuthorInput() {
    return this.authorInput.getAttribute('value');
  }

  async setSourceInput(source) {
    await this.sourceInput.sendKeys(source);
  }

  async getSourceInput() {
    return this.sourceInput.getAttribute('value');
  }

  async setUrlInput(url) {
    await this.urlInput.sendKeys(url);
  }

  async getUrlInput() {
    return this.urlInput.getAttribute('value');
  }

  async categorySelectLastOption() {
    await this.categorySelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async categorySelectOption(option) {
    await this.categorySelect.sendKeys(option);
  }

  getCategorySelect() {
    return this.categorySelect;
  }

  async getCategorySelectedOption() {
    return this.categorySelect.element(by.css('option:checked')).getText();
  }

  async save() {
    await this.saveButton.click();
  }

  async cancel() {
    await this.cancelButton.click();
  }

  getSaveButton() {
    return this.saveButton;
  }
}
