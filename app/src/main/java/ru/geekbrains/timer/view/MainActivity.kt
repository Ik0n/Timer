package ru.geekbrains.timer.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.geekbrains.timer.databinding.ActivityMainBinding
import ru.geekbrains.timer.extensions.TimestampMillisecondsFormatter
import ru.geekbrains.timer.model.TimestampProvider
import ru.geekbrains.timer.viewmodel.ElapsedTimeCalculator
import ru.geekbrains.timer.viewmodel.StopwatchListOrchestrator
import ru.geekbrains.timer.viewmodel.StopwatchStateCalculator
import ru.geekbrains.timer.viewmodel.StopwatchStateHolder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val timestampProvider = object : TimestampProvider {
        override fun getMilliseconds(): Long {
            return System.currentTimeMillis()
        }
    }

    private val stopwatchListOrchestrator = StopwatchListOrchestrator(
        StopwatchStateHolder(
            StopwatchStateCalculator(
                timestampProvider,
                ElapsedTimeCalculator(timestampProvider)
            ),
            ElapsedTimeCalculator(timestampProvider),
            TimestampMillisecondsFormatter()
        ),
        CoroutineScope(
            Dispatchers.Main + SupervisorJob()
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(
            Dispatchers.Main + SupervisorJob()
        ).launch {
            stopwatchListOrchestrator.ticker.collect {
                binding.textTime.text = it
            }
        }

        with(binding) {
            buttonStart.setOnClickListener {
                stopwatchListOrchestrator.start()
            }
            buttonPause.setOnClickListener {
                stopwatchListOrchestrator.pause()
            }
            buttonStop.setOnClickListener {
                stopwatchListOrchestrator.stop()
            }
        }

    }
}