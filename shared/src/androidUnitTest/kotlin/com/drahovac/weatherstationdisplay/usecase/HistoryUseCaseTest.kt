package com.drahovac.weatherstationdisplay.usecase

import com.drahovac.weatherstationdisplay.data.Database
import com.drahovac.weatherstationdisplay.domain.HistoryWeatherDataRepository
import com.drahovac.weatherstationdisplay.domain.historyObservationPrototype
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class HistoryUseCaseTest {

    private val scheduler = TestCoroutineScheduler()
    private val historyWeatherDataRepository: HistoryWeatherDataRepository = mockk()
    private val database: Database = mockk(relaxUnitFun = true)
    private val hasDataFlow = MutableStateFlow(false)
    private val clock = object : Clock {
        override fun now(): Instant {
            return TODAY.toInstant(TimeZone.UTC)
        }
    }
    private lateinit var historyUseCase: HistoryUseCase

    @Before
    fun setUp() {
        every { database.hasData() } returns hasDataFlow
        coEvery { database.selectHistory(any()) } returns emptyList()
        coEvery { database.selectNewestHistoryDate() } returns LOCAL_DATE
        coEvery { database.selectHistory(TODAY.date, WEEK_AFTER_TODAY) } returns listOf(HISTORY)
        coEvery { database.selectHistory(MONTH_FIRST_DAY, TODAY.date) } returns listOf(HISTORY)
        coEvery { historyWeatherDataRepository.fetchHistory(LOCAL_DATE) } returns Result.success(
            listOf(HISTORY)
        )

        historyUseCase = HistoryUseCase(historyWeatherDataRepository, database, clock)
    }

    @Test
    fun `return has data flow`() {
        assertEquals(hasDataFlow, historyUseCase.hasData)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetch history up to date`() = runTest(scheduler) {
        historyUseCase.fetchHistoryUpToDate()

        coVerify { database.selectNewestHistoryDate() }
        coVerify { historyWeatherDataRepository.fetchHistory(LOCAL_DATE) }
        scheduler.advanceTimeBy(1)
        coVerify { database.insertHistoryObservations(listOf(HISTORY)) }
    }

    @Test
    fun `do not fetch history up to date if already downloaded`() = runTest(scheduler) {
        coEvery { database.selectNewestHistoryDate() } returns TODAY.date

        historyUseCase.fetchHistoryUpToDate()

        coVerify { database.selectNewestHistoryDate() }
        coVerify(exactly = 0) { historyWeatherDataRepository.fetchHistory(any()) }
    }

    @Test
    fun `fetch history and split request per month max`() = runTest(scheduler) {
        val dateBefore3months = LocalDate.parse("2023-05-30")
        val dateBefore2months = LocalDate.parse("2023-06-30")
        val dateBeforeMonth = LocalDate.parse("2023-07-30")
        coEvery { historyWeatherDataRepository.fetchHistory(any()) } returns Result.success(
            listOf(HISTORY)
        )
        coEvery { historyWeatherDataRepository.fetchHistory(any(), any()) } returns Result.success(
            listOf(HISTORY)
        )

        historyUseCase.fetchHistory(dateBefore3months)


        coVerifySequence {
            historyWeatherDataRepository.fetchHistory(
                dateBefore3months,
                dateBefore2months
            )
            historyWeatherDataRepository.fetchHistory(
                dateBefore2months,
                dateBeforeMonth
            )
            historyWeatherDataRepository.fetchHistory(
                dateBeforeMonth
            )
        }
    }

    @Test
    fun `return week history`() = runTest(scheduler) {
        val result = historyUseCase.getWeekHistory()

        coVerify { database.selectHistory(TODAY.date, WEEK_AFTER_TODAY) }

        assertEquals(
            listOf(
                HISTORY,
            ), result
        )

    }

    @Test
    fun `return month history`() = runTest(scheduler) {
        historyUseCase.getMonthHistory()

        coVerify { database.selectHistory(MONTH_FIRST_DAY, TODAY.date) }
    }

    private companion object {
        val LOCAL_DATE = LocalDate.parse("2023-07-03")
        val WEEK_AFTER_TODAY = LocalDate.parse("2023-08-07")
        val TODAY = LocalDateTime.parse("2023-07-31T03:06")
        val MONTH_FIRST_DAY = LocalDate.parse("2023-07-01")
        val HISTORY = historyObservationPrototype.copy(obsTimeUtc = TODAY.toInstant(TimeZone.UTC))
    }
}