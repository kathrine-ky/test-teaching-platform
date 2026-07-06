import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
    token: localStorage.getItem('token') || ''
  },
  mutations: {
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    LOGOUT(state) {
      state.userInfo = null
      state.token = ''
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
    }
  },
  getters: {
    isTeacher: state => state.userInfo?.role === 'TEACHER',
    isStudent: state => state.userInfo?.role === 'STUDENT',
    userId: state => state.userInfo?.userId,
    userName: state => state.userInfo?.realName || state.userInfo?.username
  }
})
