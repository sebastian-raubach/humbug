/*
 *  Copyright 2017 Information and Computational Sciences,
 *  The James Hutton Institute.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package jhi.humbug.gui.i18n;

import java.util.*;

/**
 * {@link RB} wraps the {@link ResourceBundle} of this application. Use {@link #getString(String, Object...)} and the constants of this class to
 * access the resources.
 *
 * @author Sebastian Raubach
 */
public class RB extends jhi.swtcommons.gui.i18n.RB
{
	public static final String APPLICATION_TITLE           = "application.title";
	public static final String APPLICATION_TITLE_NO_SPACES = "application.title.no.spaces";

	public static final String GENERAL_DELETE = "general.delete";
	public static final String GENERAL_BROWSE = "general.browse";

	public static final String QUESTION_CONFIRM_CLEAR = "question.confirm.clear";

	public static final String ERROR_INPUT       = "error.input";
	public static final String ERROR_INPUT_EMPTY = "error.input.empty";

	public static final String ERROR_IMPORT_FILE_NOT_EXISTS = "error.import.file.not.exists";
	public static final String ERROR_IMPORT_FILE_IS_FOLDER  = "error.import.file.is.folder";

	public static final String ERROR_ABOUT_LICENSE = "error.about.license";

	public static final String ERROR_CLIPBOARD_EMPTY = "error.clipboard.empty";

	public static final String ERROR_NO_SOURCE_FOLDER_SELECTED = "error.no.source.folder.selected";

	public static final String INFORMATION_SAVE = "information.save";

	public static final String INFORMATION_NO_UPDATE_AVAILABLE = "information.no.update.available";

	public static final String TOOLTIP_BUTTON_ADD             = "tooltip.button.add";
	public static final String TOOLTIP_BUTTON_CLEAR           = "tooltip.button.clear";
	public static final String TOOLTIP_BUTTON_PDF             = "tooltip.button.pdf";
	public static final String TOOLTIP_BUTTON_ASSOCIATE_IMAGE = "tooltip.button.associate.image";
	public static final String TOOLTIP_BUTTON_SELECT          = "tooltip.button.image.select";
	public static final String TOOLTIP_BUTTON_OPEN            = "tooltip.button.image.open";

	public static final String BUTTON_PDF   = "button.pdf";
	public static final String BUTTON_CLEAR = "button.clear";
	public static final String BUTTON_ADD   = "button.add";

	public static final String DIALOG_BUTTON_RUN = "dialog.button.run";

	public static final String MENU_MAIN_FILE                   = "menu.main.file";
	public static final String MENU_MAIN_FILE_IMPORT            = "menu.main.file.import";
	public static final String MENU_MAIN_FILE_IMPORT_TXT_FILE   = "menu.main.file.import.txt.file";
	public static final String MENU_MAIN_FILE_IMPORT_XML_FILE   = "menu.main.file.import.xml.file";
	public static final String MENU_MAIN_FILE_IMPORT_CLIPBOARD  = "menu.main.file.import.clipboard";
	public static final String MENU_MAIN_FILE_EXPORT_XML_FILE   = "menu.main.file.export.xml.file";
	public static final String MENU_MAIN_FILE_BULK_IMAGE_RENAME = "menu.main.file.bulk.image.rename";
	public static final String MENU_MAIN_FILE_EXIT              = "menu.main.file.exit";
	public static final String MENU_MAIN_HELP                   = "menu.main.help";
	public static final String MENU_MAIN_HELP_ONLINE_HELP       = "menu.main.help.online.help";
	public static final String MENU_MAIN_HELP_SETTINGS          = "menu.main.help.settings";
	public static final String MENU_MAIN_HELP_UPDATE            = "main.menu.help.update";
	public static final String MENU_MAIN_HELP_ABOUT             = "menu.main.help.about";

	public static final String MENU_CONTEXT_IMAGE_SAVE      = "menu.context.image.save";
	public static final String MENU_CONTEXT_IMAGE_CLIPBOARD = "menu.context.image.clipboard";

	public static final String THREAD_IMPORT_TITLE = "thread.import.title";
	public static final String THREAD_IMPORT_ROW   = "thread.import.row";

	public static final String THREAD_RENAME_IMAGE = "thread.rename.image";

	public static final String THREAD_EXPORT_TITLE = "thread.export.title";

	public static final String DIALOG_CLIPBOARD_TITLE = "dialog.clipboard.title";

	public static final String DIALOG_RENAME_IMAGE_FILE_TITLE          = "dialog.rename.image.file.title";
	public static final String DIALOG_RENAME_IMAGE_FILE_DESCRIPTION    = "dialog.rename.image.file.description";
	public static final String DIALOG_RENAME_IMAGE_OPTIONS_TITLE       = "dialog.rename.image.options.title";
	public static final String DIALOG_RENAME_IMAGE_OPTIONS_DESCRIPTION = "dialog.rename.image.options.description";

	public static final String DIALOG_RENAME_IMAGE_NO_BARCODE_TITLE   = "dialog.rename.image.no.barcode.title";
	public static final String DIALOG_RENAME_IMAGE_NO_BARCODE_MESSAGE = "dialog.rename.image.no.barcode.message";
	public static final String DIALOG_RENAME_IMAGE_SOURCE             = "dialog.rename.image.source";
	public static final String DIALOG_RENAME_IMAGE_TARGET             = "dialog.rename.image.target";
	public static final String DIALOG_RENAME_IMAGE_TITLE              = "dialog.rename.image.title";

	public static final String DIALOG_SETTINGS_TITLE                  = "dialog.settings.title";
	public static final String DIALOG_SETTINGS_GENERAL_TITLE          = "dialog.settings.general.title";
	public static final String DIALOG_SETTINGS_GENERAL_LOCALE_TITLE   = "dialog.settings.general.locale.title";
	public static final String DIALOG_SETTINGS_GENERAL_LOCALE_MESSAGE = "dialog.settings.general.locale.message";
	public static final String DIALOG_SETTINGS_GENERAL_UPDATE_TITLE   = "dialog.settings.general.update.title";
	public static final String DIALOG_SETTINGS_GENERAL_UPDATE_MESSAGE = "dialog.settings.general.update.message";
	public static final String DIALOG_SETTINGS_RESTART_REQUIRED       = "dialog.settings.restart.required";
	public static final String DIALOG_SETTINGS_DO_RESTART             = "dialog.settings.do.restart";
	public static final String DIALOG_SETTINGS_BARCODE_TYPE_TITLE     = "dialog.settings.barcode.type.title";
	public static final String DIALOG_SETTINGS_BARCODE_TYPE_MESSAGE   = "dialog.settings.barcode.type.message";
	public static final String DIALOG_SETTINGS_MARGIN_TITLE           = "dialog.settings.margin.title";
	public static final String DIALOG_SETTINGS_MARGIN_MESSAGE         = "dialog.settings.margin.message";
	public static final String DIALOG_SETTINGS_PAGE_LAYOUT_TITLE      = "dialog.settings.layout.title";
	public static final String DIALOG_SETTINGS_PADDING_TITLE          = "dialog.settings.padding.title";
	public static final String DIALOG_SETTINGS_PADDING_MESSAGE        = "dialog.settings.padding.message";
	public static final String DIALOG_SETTINGS_IMAGE_HEIGHT_TITLE     = "dialog.settings.image.height.title";

	public static final String DIALOG_SETTINGS_IMAGE_HEIGHT_MESSAGE = "dialog.settings.image.height.message";
	public static final String DIALOG_ABOUT_TITLE                   = "dialog.about.title";
	public static final String DIALOG_ABOUT_DESCRIPTION             = "dialog.about.description";
	public static final String DIALOG_ABOUT_ADDITIONAL              = "dialog.about.additional";
	public static final String DIALOG_ABOUT_COPYRIGHT               = "dialog.about.copyright";
	public static final String DIALOG_ABOUT_EMAIL_TITLE             = "dialog.about.email.title";
	public static final String DIALOG_ABOUT_EMAIL_URL               = "dialog.about.email.url";
	public static final String DIALOG_ABOUT_WEBSITE_TITLE           = "dialog.about.website.title";
	public static final String DIALOG_ABOUT_WEBSITE_URL             = "dialog.about.website.url";
	public static final String DIALOG_ABOUT_JHI_URL                 = "dialog.about.jhi.url";
	public static final String DIALOG_ABOUT_TWITTER_TITLE           = "dialog.about.twitter.title";

	public static final String DIALOG_ABOUT_TWITTER_URL = "dialog.about.twitter.url";
	public static final String DIALOG_ABOUT_TAB_LICENSE = "dialog.about.tab.license";

	public static final String DIALOG_ABOUT_TAB_ABOUT = "dialog.about.tab.about";

	public static final String DIALOG_IMAGE_ASSOCIATION_TITLE               = "dialog.image.association.title";
	public static final String SETTING_BARCODE_RENAME_DUPLICATE_TITLE       = "setting.barcode.rename.duplicate.title";
	public static final String SETTING_BARCODE_RENAME_DUPLICATE_CONCATENATE = "setting.barcode.rename.duplicate.concatenate";

	public static final String SETTING_BARCODE_RENAME_DUPLICATE_PICK_FIRST = "setting.barcode.rename.duplicate.pick.first";
	public static final String SETTING_BARCODE_RENAME_MISSING_TITLE        = "setting.barcode.rename.missing.title";
	public static final String SETTING_BARCODE_RENAME_MISSING_SKIP         = "setting.barcode.rename.missing.skip";

	public static final String SETTING_BARCODE_RENAME_RESTRICT_TYPE_TITLE      = "setting.barcode.rename.restrict.type.title";
	public static final String SETTING_BARCODE_RENAME_RESTRICT_TYPE_ACCEPT_ALL = "setting.barcode.rename.restrict.type.accept.all";

	public static final String SETTING_BARCODE_RENAME_TRY_HARD_TITLE = "setting.barcode.rename.try.hard.title";
	public static final String SETTING_BARCODE_RENAME_TRY_HARD_TEXT  = "setting.barcode.rename.try.hard.text";

	public static final String URL_ONLINE_HELP = "url.online.help";

	public static final String       SETTING_BARCODE_RENAME_MISSING_COPY = "setting.barcode.rename.missing.copy";
	public static final List<Locale> SUPPORTED_LOCALES                   = new ArrayList<>();

	static
	{
		SUPPORTED_LOCALES.add(Locale.ENGLISH);
		SUPPORTED_LOCALES.add(Locale.GERMAN);
	}
}
