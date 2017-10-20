/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  Button,
  View
} from 'react-native';
import Header from  './src/components/header';
import SignUPIn from  './src/components/SignUpIn';

export default class App extends Component<{}> {
  render() {
    return (
      <View style={styles.container}>
        <Header />
        <SignUPIn />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'column',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
});
