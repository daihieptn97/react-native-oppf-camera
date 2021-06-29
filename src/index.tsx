import { NativeModules } from 'react-native';

type OppfCameraType = {
  multiply(a: number, b: number): Promise<number>;
};

const { OppfCamera } = NativeModules;

export default OppfCamera as OppfCameraType;
