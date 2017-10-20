// import libraries
import React, { Component } from 'react';
import { View, Text, StyleSheet} from 'react-native';
import LoginForm from './LoginForm';
// create a Component
class Login extends Component {
  render () {
    return {
      <View style = {styles.container}>
        <Text>Login</Text>
      </View>
    };
  }
}
    <KeyboardAvoidingView behavior="padding" style={styles.container}>
        <View style={styles.loginContainer}>
            <Image resizeMode="contain" style={styles.logo} source={require('../../components/images/logo-dark-bg.png')} />
         </View>
        <View style={styles.formContainer}>
            <LoginForm />
        </View>
    </KeyboardAvoidingView>


    // define your styles
    const styles = StyleSheet.create({
        container: {
            flex: 1,
            backgroundColor: '#2c3e50',
        },
        loginContainer:{
            alignItems: 'center',
            flexGrow: 1,
            justifyContent: 'center'
        },
        logo: {
            position: 'absolute',
            width: 300,
            height: 100
        }
