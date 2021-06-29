import { NativeModules } from 'react-native';

type OppfCameraType = {
    registerOnFunDeviceWiFiConfigListener: (callBack: (data: string) => void) => void;
    onSmartConfig: (wifiName: string, passWifi: string) => void
};

const { OppfCamera } = NativeModules;

export default OppfCamera as OppfCameraType;
