/* tslint:disable no-unused-expression */
import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import TidbitComponentsPage from './tidbit.page-object';
import { TidbitDeleteDialog } from './tidbit.page-object';
import TidbitUpdatePage from './tidbit-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Tidbit e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let tidbitUpdatePage: TidbitUpdatePage;
  let tidbitComponentsPage: TidbitComponentsPage;
  let tidbitDeleteDialog: TidbitDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();

    await signInPage.username.sendKeys('admin');
    await signInPage.password.sendKeys('admin');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();
    await waitUntilDisplayed(navBarPage.entityMenu);
  });

  it('should load Tidbits', async () => {
    await navBarPage.getEntityPage('tidbit');
    tidbitComponentsPage = new TidbitComponentsPage();
    expect(await tidbitComponentsPage.getTitle().getText()).to.match(/Tidbits/);
  });

  it('should load create Tidbit page', async () => {
    await tidbitComponentsPage.clickOnCreateButton();
    tidbitUpdatePage = new TidbitUpdatePage();
    expect(await tidbitUpdatePage.getPageTitle().getAttribute('id')).to.match(/tidbitsApp.tidbit.home.createOrEditLabel/);
  });

  it('should create and save Tidbits', async () => {
    const nbButtonsBeforeCreate = await tidbitComponentsPage.countDeleteButtons();

    await tidbitUpdatePage.setContentInput('content');
    expect(await tidbitUpdatePage.getContentInput()).to.match(/content/);
    await tidbitUpdatePage.setAuthorInput('author');
    expect(await tidbitUpdatePage.getAuthorInput()).to.match(/author/);
    await tidbitUpdatePage.setSourceInput('source');
    expect(await tidbitUpdatePage.getSourceInput()).to.match(/source/);
    await tidbitUpdatePage.setUrlInput('url');
    expect(await tidbitUpdatePage.getUrlInput()).to.match(/url/);
    await tidbitUpdatePage.categorySelectLastOption();
    await waitUntilDisplayed(tidbitUpdatePage.getSaveButton());
    await tidbitUpdatePage.save();
    await waitUntilHidden(tidbitUpdatePage.getSaveButton());
    expect(await tidbitUpdatePage.getSaveButton().isPresent()).to.be.false;

    await tidbitComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await tidbitComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last Tidbit', async () => {
    await tidbitComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await tidbitComponentsPage.countDeleteButtons();
    await tidbitComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    tidbitDeleteDialog = new TidbitDeleteDialog();
    expect(await tidbitDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/tidbitsApp.tidbit.delete.question/);
    await tidbitDeleteDialog.clickOnConfirmButton();

    await tidbitComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await tidbitComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
