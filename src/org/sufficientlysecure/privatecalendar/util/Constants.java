/**
 *  Copyright (C) 2012  Dominik Schürmann <dominik@dominikschuermann.de>
 *  Copyright (C) 2012  Harald Seltner <h.seltner@gmx.at>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sufficientlysecure.privatecalendar.util;

import android.accounts.Account;

public class Constants {
    public static final boolean DEBUG = false;

    public static final String TAG = "Private Calendar";

    public static final String ACCOUNT_NAME = "Private Calendar";
    public static final String ACCOUNT_TYPE = "org.sufficientlysecure.privatecalendar.account";
    public static final String CONTENT_AUTHORITY = "com.android.calendar";

    public static final Account ACCOUNT = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
}