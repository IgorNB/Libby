import React, {Component} from "react";
import {connect} from "react-redux";
import Button from "@material-ui/core/Button";
import {changeLocale as changeLocaleAction} from "react-admin";

class LocaleSwitcher extends Component {
    switchToRussian = () => this.props.changeLocale("ru");
    switchToEnglish = () => this.props.changeLocale("en");

    render() {
        const {locale} = this.props;
        return (
            <div>
                <Button
                    variant="raised"
                    color={locale === "en" ? "primary" : "default"}
                    onClick={this.switchToEnglish}>en</Button>
                <Button
                    variant="raised"
                    color={locale === "ru" ? "primary" : "default"}
                    onClick={this.switchToRussian}>ru</Button>
            </div>
        );
    }
}

export default connect(
    state => ({
        locale: state.i18n.locale
    }),
    {changeLocale: changeLocaleAction}
)(LocaleSwitcher);