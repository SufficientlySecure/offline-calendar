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
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class AccountHelper {

    Context mContext;

    public AccountHelper(Context context) {
        mContext = context;
    }

    /**
     * Add account for Birthday Adapter to Android system
     * 
     * @param context
     * @return
     */
    public Bundle addAccount() {
        Log.d(Constants.TAG, "Adding account...");

        AccountManager am = AccountManager.get(mContext);
        if (am.addAccountExplicitly(Constants.ACCOUNT, null, null)) {
            Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, Constants.ACCOUNT.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT.type);
            return result;
        } else {
            return null;
        }
    }

    /**
     * Checks whether the account is enabled or not
     * 
     * @param context
     * @return
     */
    public boolean isAccountActivated() {
        AccountManager am = AccountManager.get(mContext);

        Account[] availableAccounts = am.getAccountsByType(Constants.ACCOUNT_TYPE);
        for (Account currentAccount : availableAccounts) {
            if (currentAccount.name.equals(Constants.ACCOUNT_NAME)) {
                return true;
            }
        }

        return false;
    }
}
