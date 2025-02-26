import { useState } from "react"
import { Button, ButtonVariant, Form, FormGroup, FormSelectOption, FormSelect, Modal } from "@patternfly/react-core"
import { Hook } from "../../api"
import HookUrlSelector from "../../components/HookUrlSelector"
import { globalEventTypes } from "./reducers"

type AddHookModalProps = {
    isOpen: boolean
    onClose(): void
    onSubmit(hook: Hook): Promise<any>
}

function AddHookModal(props: AddHookModalProps) {
    const [url, setUrl] = useState("")
    const [eventType, setEventType] = useState(globalEventTypes[0])
    const [isSaving, setSaving] = useState(false)

    const onClose = () => {
        setUrl("")
        setEventType(globalEventTypes[0])
        setSaving(false)
        props.onClose()
    }
    const [isValid, setValid] = useState(false)

    return (
        <Modal
            title="New Hook"
            isOpen={props.isOpen}
            onClose={onClose}
            actions={[
                <Button
                    key="save"
                    variant={ButtonVariant.primary}
                    isDisabled={isSaving || !isValid}
                    onClick={() => {
                        setSaving(true)
                        props
                            .onSubmit({
                                id: 0,
                                url: url.trim(),
                                type: eventType,
                                target: -1,
                                active: true,
                            })
                            .finally(onClose)
                    }}
                >
                    Save
                </Button>,
                <Button key="cancel" variant={ButtonVariant.link} isDisabled={isSaving} onClick={onClose}>
                    Cancel
                </Button>,
            ]}
        >
            <Form isHorizontal={true}>
                <HookUrlSelector
                    active={props.isOpen}
                    value={url}
                    setValue={setUrl}
                    isDisabled={isSaving}
                    setValid={setValid}
                />
                <FormGroup label="Event Type" isRequired={true} fieldId="type" helperText="event type for callback">
                    <FormSelect
                        id="type"
                        value={eventType}
                        onChange={setEventType}
                        aria-label="Event Type"
                        isDisabled={isSaving}
                    >
                        {globalEventTypes.map((option, index) => {
                            return <FormSelectOption key={index} value={option} label={option} />
                        })}
                    </FormSelect>
                </FormGroup>
            </Form>
        </Modal>
    )
}

export default AddHookModal
