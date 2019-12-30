/**
 * License controller
 *
 * @author Manohar Viswanathan
 */
class LicenseController {

    def licenseService

    def index = {
        switch (request.method) {
            case 'GET':
                def license = licenseService.getLicense()
                [license: license]
                break
            case 'POST':
                def license = licenseService.setLicense(params)
                [license: license]
                break
        }
    }


}
