package br.edu.ifsp.simplesdatastore

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import br.edu.ifsp.simplesdatastore.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime

private val Context.dataStore by preferencesDataStore(MainActivity.FILE_NAME)

class MainActivity : AppCompatActivity() {
    companion object{
        const val FILE_NAME = "user_prefs"
        private val KEY_NAME = stringPreferencesKey("update_time")
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObserver()
    }

    override fun onStop() {
        record()
        super.onStop()
    }

    private fun setupObserver() {
        val updateFlow: Flow<String?> = dataStore.data
            .map { preferences ->
                preferences.get(KEY_NAME)
            }

        lifecycleScope.launch {
            updateFlow.collect{ update ->
                binding.textLastUpdate.text = update ?: "Primeiro uso."
            }
        }
    }

    private fun record(){
        lifecycleScope.launch {
            dataStore.edit { preferences ->
                preferences.set(KEY_NAME, LocalDateTime.now().toString())
            }
        }
    }
}