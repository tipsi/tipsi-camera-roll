import helper from 'tipsi-appium-helper'
import test from './utils/tape'

const { driver, idFromAccessId, platform } = helper

async function successSave(t) {
  const successId = idFromAccessId('Success')
  await driver.waitForVisible(successId, 30000)
  t.pass('Image should be saved into photo gallery')
}

test('Test save image', async (t) => {
  const saveFromUrlId = idFromAccessId('saveFromUrl')
  const saveFromPathId = idFromAccessId('saveFromPath')
  const saveBase64Id = idFromAccessId('saveBase64')
  const allowId = idFromAccessId('OK')

  await driver.waitForVisible(saveFromUrlId, 30000)
  t.pass('User should see `Save from URL` button')

  await driver.click(saveFromUrlId)
  t.pass('User should be able click on `Save from URL` button')

  if (platform('ios')) {
    await driver.waitForVisible(allowId, 10000)
    await driver.click(allowId)
    t.pass('User should be able agree to use Photo library')
  }

  await successSave(t)

  await driver.waitForVisible(saveFromPathId, 30000)
  t.pass('User should see `Save from path` button')

  await driver.click(saveFromPathId)
  t.pass('User should be able click on `Save from path` button')
  await successSave(t)


  await driver.waitForVisible(saveBase64Id, 30000)
  t.pass('User should see `Save from base64` button')

  await driver.click(saveBase64Id)
  t.pass('User should be able click on `Save from base64` button')
  await successSave(t)
})
