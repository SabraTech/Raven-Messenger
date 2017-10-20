/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {Text,  View, StyleSheet } from 'react-native';

export default class Header extends Component<{}>{
  render(){
    return (
     
        <View style={styles.navBar}>
          <Text style={styles.navBarButton}>Back</Text>
          <Text style={styles.navBarHeader}>Chatting Bot</Text>
          <Text style={styles.navBarButton}>More</Text>
        </View>
    );
  }
};

const styles = StyleSheet.create({
 
  navBar: {
    flexDirection: 'row',
    paddingTop: 15,
    height: 60,
    backgroundColor: '#841584',
    shadowColor:"#000",
    shadowOffset:{width:0,height:2},
    shadowCapacity:0.2,
    position:'relative'
  },
    navBarButton: {
    color: '#FFFFFF',
    textAlign:'center',
    width: 64
  },
  navBarHeader: {
    flex: 1,
    color: '#FFFFFF',
    fontWeight: 'bold',
    textAlign: 'center'
  },

});
