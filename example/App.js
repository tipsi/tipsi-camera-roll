import React, { Component } from 'react'
import { StyleSheet, Text, View, Image, TouchableHighlight } from 'react-native'
import CameraRoll from 'tipsi-camera-roll'
import RNFS from 'react-native-fs'
import testID from './src/utils/testID'

const IMAGE_URL = 'https://strv.ghost.io/content/images/2016/08/1200x628.png'

export default class App extends Component {
  state = {
    saved: false,
    error: null,
  }

  resetState = () => {
    this.setState({
      saved: false,
      error: null,
    })
  }

  saveFromUrlHandler = () => {
    this.resetState()
    CameraRoll.saveToCameraRoll(IMAGE_URL, 'tipsi')
      .then(() => {
        this.setState({ saved: true })
      })
      .catch((error) => {
        this.setState({ error: error.message })
      })
  }

  saveFromPathHandler = () => {
    this.resetState()
    const filePath = `${RNFS.DocumentDirectoryPath}/test.jpg`
    const options = {
      fromUrl: IMAGE_URL,
      toFile: filePath,
      background: true,
    }

    RNFS.downloadFile(options).promise
      .then(() => {
        CameraRoll.saveToCameraRoll(IMAGE_URL, 'Path')
          .then(() => {
            this.setState({ saved: true })
          })
          .catch((error) => {
            this.setState({ error: error.message })
          })
      })
      .catch((error) => {
        this.setState({ error: error.message })
      })
  }

  saveBase64Handler = () => {
    this.resetState()
    const filePath = `${RNFS.DocumentDirectoryPath}/test.jpg`
    const options = {
      fromUrl: IMAGE_URL,
      toFile: filePath,
      background: true,
    }

    RNFS.downloadFile(options).promise
      .then(() => {
        RNFS.readFile(filePath, 'base64')
          .then((base64) => {
            const uri = `data:image/jpeg;base64,${base64}`
            CameraRoll.saveToCameraRoll(uri, 'Base64')
              .then(() => {
                this.setState({ saved: true })
              })
              .catch((error) => {
                this.setState({ error: error.message })
              })
          })
      })
      .catch((error) => {
        this.setState({ error: error.message })
      })
  }

  render() {
    return (
      <View style={styles.container}>
        <Image
          source={{ uri: IMAGE_URL }}
          style={styles.image}
        />
        <TouchableHighlight onPress={this.saveFromUrlHandler} {...testID('saveFromUrl')}>
          <View style={styles.button}>
            <Text>Save from URL</Text>
          </View>
        </TouchableHighlight>
        <TouchableHighlight onPress={this.saveFromPathHandler} {...testID('saveFromPath')}>
          <View style={styles.button}>
            <Text>Save from path</Text>
          </View>
        </TouchableHighlight>
        <TouchableHighlight onPress={this.saveBase64Handler} {...testID('saveBase64')}>
          <View style={styles.button}>
            <Text>Save from base64</Text>
          </View>
        </TouchableHighlight>
        {this.state.saved &&
          <View style={styles.saved} {...testID('Success')}><Text>Saved to album</Text></View>}
        {this.state.error &&
          <View style={styles.error}><Text>{this.state.error}</Text></View>}
      </View>
    )
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  button: {
    marginTop: 20,
    width: 150,
    height: 50,
    backgroundColor: '#A3A3A3',
    borderRadius: 10,
    alignItems: 'center',
    justifyContent: 'center',
  },
  saved: {
    marginTop: 15,
    backgroundColor: 'green',
  },
  error: {
    marginTop: 15,
    backgroundColor: 'red',
  },
  image: {
    width: 200,
    height: 100,
  },
})
