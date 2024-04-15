package com.example.crumbly.ui.main.views

import android.app.Application
import android.content.Context
import com.example.crumbly.MainActivity
import com.example.crumbly.ui.main.database.AppScope
import com.example.crumbly.ui.main.database.RecipeDao
import com.example.crumbly.ui.main.database.RecipeDatabase
import dagger.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


// Definition of the Application graph
@AppScope
@Component(modules = [RepositoryModule::class, ApplicationModule::class])
interface ApplicationComponent {

    @AppScope
    fun inject(activity: MainActivity)

}

@Module
class ApplicationModule(var app: Application) {

    @Provides
    fun provideApp(): Application = app

    @Provides
    fun provideContext(): Context = app.applicationContext

    @Provides
    fun provideCororutineScope(): CoroutineScope = CoroutineScope(SupervisorJob())
}

@Module
class RepositoryModule {

    @Provides
    fun provideRecipeDao(database: RecipeDatabase): RecipeDao {
        return database.recipeDao();
    }

    @Provides
    fun provideYourDatabase(
        app: Context,
        coroutineScope: CoroutineScope
    ) = RecipeDatabase.getDatabase(app, coroutineScope)
}


class MainApplication : Application() {
    lateinit var appComponent: ApplicationComponent

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    override fun onCreate() {
        super.onCreate()
        appComponent =
            DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }
}

