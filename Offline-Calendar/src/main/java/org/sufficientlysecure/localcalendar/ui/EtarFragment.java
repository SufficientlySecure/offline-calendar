/*
 * Copyright (C) 2013-2016 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sufficientlysecure.localcalendar.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.sufficientlysecure.localcalendar.R;

public class EtarFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.etar_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button buttonInstall = view.findViewById(R.id.buttonInstallEtar);
        Button skip = view.findViewById(R.id.buttonSkip);
        buttonInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent installIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=ws.xsoh.etar"));

                // launch market
                if (installIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(installIntent);
                } else {
                    // no f-droid market app or Play store installed -> launch browser for f-droid url
                    Intent downloadIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://f-droid.org/repository/browse/?fdid=ws.xsoh.etar"));
                    if (downloadIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(downloadIntent);
                    } else {
                        Toast.makeText(getActivity(), "No browser available!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showCalendars();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
