package com.meowbox.progressions

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.meowbox.DateTime
import com.meowbox.progressions.db.Db
import com.meowbox.progressions.db.loadChart
import com.meowbox.progressions.db.loadDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        loadDatabase(applicationContext, Db.databaseName)
        Db.dbSetup(AndroidSqliteDriver(Database.Schema, applicationContext, Db.databaseName))
        dorthBurgle.text = loadChart(DateTime(1978, 4, 7, 15, 30)).dayPillar.branch.name
    }
}