import * as React from 'react';

import { StyleSheet, Text, TextInput, TouchableOpacity, View } from 'react-native';
import OppfCamera from 'react-native-oppf-camera';

export default function App() {
    // const [result, setResult] = React.useState<number | undefined>();

    React.useEffect(() => {
        // OppfCamera.multiply(3, 7).then(setResult);
        console.log('DEBUG', OppfCamera);

        // OppfCamera.registerOnFunDeviceWiFiConfigListener((data) => {
        //     console.log(data);
        // });
    }, []);

    let onStartConfig = () => {
        OppfCamera.onSmartConfig('Hunonic T2_2Ghz', '66668888', data => {
            console.log(data);
        });
    };
    return (
        <View style={styles.container}>
            <View style={{ marginHorizontal: 14 }}>
                <TextInput
                    style={styles.inputStyle}
                    placeholder={'wifi name'} />
                <TextInput
                    style={styles.inputStyle}
                    placeholder={'password'} />
            </View>
            <TouchableOpacity
                onPress={onStartConfig}
                style={styles.buttonStyle}>
                <Text>SmartConfig</Text>
            </TouchableOpacity>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,

        justifyContent: 'center',
    },
    box: {
        width: 60,
        height: 60,
        marginVertical: 20,
    },
    buttonStyle: {
        backgroundColor: '#bebebe',
        justifyContent: 'center',
        alignItems: 'center',
        height: 50,
        width: '60%',
        borderRadius: 4,
        alignSelf: 'center',

        shadowColor: 'grey',

        shadowOffset: {
            width: 0,
            height: 1,
        },
        shadowOpacity: 0.22,
        shadowRadius: 2.22,

        elevation: 3,
    }, inputStyle: {
        borderWidth: 1,
        borderRadius: 5,
        borderColor: '#bebebe',
        marginBottom: 10,
        paddingHorizontal: 14,
    },


});
