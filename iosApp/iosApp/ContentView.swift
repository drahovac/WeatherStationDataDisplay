import SwiftUI
import shared
import KMMViewModelSwiftUI

struct ContentView: View {
    
    @StateViewModel var viewModel: SetupDeviceIdViewModel = SetupDeviceIdViewModel(credentialsRepository: CreateSetUpDeviceIdViewModelHelper().credentialsRepository)

	var body: some View {
        VStack{
            let title = MR.strings().setup_welcome
            let header = MR.strings().setup_enter_device_id
            Text(LocalizedStringKey(title.resourceId),
                 bundle: title.bundle)
            .font(.largeTitle)
            .padding()
            Text(LocalizedStringKey(header.resourceId),bundle: header.bundle).padding(.bottom,8)
            Spacer()
        }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
