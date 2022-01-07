package ru.ra66it.updaterforspotify.presentation.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.ra66it.updaterforspotify.R
import ru.ra66it.updaterforspotify.UpdaterApp
import ru.ra66it.updaterforspotify.github_link
import ru.ra66it.updaterforspotify.paypal_link
import ru.ra66it.updaterforspotify.presentation.ui.screen.SettingsGroup
import ru.ra66it.updaterforspotify.presentation.ui.screen.SettingsMenuItem
import ru.ra66it.updaterforspotify.presentation.ui.screen.SettingsSwitch
import ru.ra66it.updaterforspotify.presentation.utils.openLink
import ru.ra66it.updaterforspotify.presentation.viewmodel.SettingsViewModel
import javax.inject.Inject

class SettingsActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UpdaterApp.applicationComponent.inject(this)

        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        setContent {
            val titleStyle = TextStyle(
                color = colorResource(id = R.color.colorAccent),
                fontWeight = FontWeight.SemiBold
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.colorPrimaryDark)),
            ) {
                SettingsGroup(
                    title = {
                        Text(
                            text = getString(R.string.general)
                        )
                    },
                    titleStyle = titleStyle
                ) {
                    SettingsSwitch(
                        title = {
                            Text(
                                text = getString(R.string.enable_notification),
                                color = Color.White
                            )
                        },
                        subtitle = {
                            Text(
                                text = getString(R.string.notification_text_origin),
                                color = Color.Gray
                            )
                        },
                        isChecked = viewModel.isEnableNotification
                    ) {
                        viewModel.toggleEnableNotifications(it)
                    }
                }
                Divider()
                SettingsGroup(
                    title = {
                        Text(
                            modifier = Modifier.padding(48.dp, 0.dp, 0.dp, 0.dp),
                            text = getString(R.string.info)
                        )
                    },
                    titleStyle = titleStyle
                ) {
                    SettingsMenuItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_paypal),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        },
                        title = {
                            Text(text = getString(R.string.donate), color = Color.White)
                        },
                        subtitle = {
                            Text(
                                text = getString(R.string.donate_paypal),
                                color = Color.Gray
                            )
                        }) {
                        openLink(paypal_link)
                    }
                    SettingsMenuItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_github),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        },
                        title = { Text(text = getString(R.string.github), color = Color.White) },
                        subtitle = {
                            Text(
                                text = getString(R.string.see_on_github),
                                color = Color.Gray
                            )
                        }) {
                        openLink(github_link)
                    }
                }
                Divider()
                SettingsGroup(
                    title = {
                        Text(
                            modifier = Modifier.padding(48.dp, 0.dp, 0.dp, 0.dp),
                            text = getString(R.string.about)
                        )
                    },
                    titleStyle = titleStyle
                ) {
                    SettingsMenuItem(
                        title = {
                            Text(
                                text = getString(R.string.app_version),
                                color = Color.White
                            )
                        },
                        subtitle = {
                            Text(text = viewModel.versionApp, color = Color.Gray)
                        }) {

                    }
                }

            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
