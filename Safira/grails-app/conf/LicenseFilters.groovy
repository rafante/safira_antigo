import grails.util.Environment

/**
 * License Filter
 *
 * @author Manohar Viswanathan
 */
class LicenseFilters {

    LicenseService licenseService = new LicenseService()

    def filters = {
        // intercept necessary controllers here except license controller
        all(controller: 'license', invert: true) { //enviando
            print(Environment.current)
            if(Environment.current == Environment.DEVELOPMENT)
                return true
            before = {
                if (licenseService.getLicense()?.validateSerial()) {
                    return true
                } else {
                    redirect(controller: "license")
                    return false
                }
            }
        }

    }

}