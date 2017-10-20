/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  StyleSheet,
  TouchableOpacity,
  Text,
  View
} from 'react-native';


export default class SignUPIn extends Component<{}>{
  render(){

    return (
     
         <View style={styles.container}>
         <TouchableOpacity style = {styles.buttonStyle}>
             <Text style={styles.textStyle}>Login</Text>
        </TouchableOpacity>

         <TouchableOpacity style = {styles.buttonStyle}>
             <Text style={styles.textStyle}>Sign UP</Text>
        </TouchableOpacity>
       </View>  
    );
  }
  
}

const styles = StyleSheet.create({

  container: {
    marginTop :200,
    flex: 1,
    flexDirection: 'column',
    alignItems: 'center',
  },
  textStyle:{
    alignSelf: 'center',
    color:'white',
    fontSize:24
  },
  buttonStyle: {
    marginTop :10,
    width: 200,
    backgroundColor:'#841584',
    height: 30,
    padding: 20,
    borderRadius: 5,
    borderWidth: 1,

  }
});
