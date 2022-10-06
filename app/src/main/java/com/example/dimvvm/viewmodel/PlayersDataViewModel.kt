package com.example.dimvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dimvvm.model.PlayerResult
import com.example.dimvvm.network.PlayerDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayersDataViewModel @Inject constructor(
    private val playersRepository: PlayerDataRepository
) : ViewModel() {

    val _playersStateData: MutableStateFlow<PlayerResult> =
        MutableStateFlow(PlayerResult.Loading)

    var playersStateData = _playersStateData

    init {
        viewModelScope.launch {
            _playersStateData.value = PlayerResult.Loading
            playersRepository.getPlayersData()
                .flowOn(Dispatchers.IO)
                .catch { exception ->
                    _playersStateData.value = PlayerResult.Error(exception.message!!)
                }
                .collect { pData ->
                    _playersStateData.value = PlayerResult.Success(pData)
                }
        }
    }
}